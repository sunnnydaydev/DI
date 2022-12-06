# @Inject注解

这里只谈论@Inject注解，不谈论@binds，@provider！！！

###### 1、@Inject标注无参构造

```kotlin
class UserRepository @Inject constructor()
```

Dagger会自动为目标类生成工厂代码 ->

```java
public final class UserRepository_Factory implements Factory<UserRepository> {
    @Override
    public UserRepository get() {
        return newInstance();
    }

    public static UserRepository_Factory create() {
        return InstanceHolder.INSTANCE;
    }

    public static UserRepository newInstance() {
        return new UserRepository();
    }

    private static final class InstanceHolder {
        private static final UserRepository_Factory INSTANCE = new UserRepository_Factory();
    }
}
```

```kotlin
//方式1：通过newInstance方法
val userRepository1 = UserRepository_Factory.newInstance()
//方式2：通过create-get方法
val userRepository2 = UserRepository_Factory.create().get()
```

工厂类提供了两种方案来创建UserRepository目标类对象实例newInstance方式、create-get方式。 create-get方式最终还是通过newInstance方式new了一个对象。

###### 2、@Inject标注有参构造

```kotlin
//目标类
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
)
//依赖项
class UserLocalDataSource @Inject constructor()
```

构造中的参数就是依赖项，@Inject标注的构造有依赖项时其依赖项也要使用@Inject标注，否则dagger编译报错：

UserLocalDataSource cannot be provided without an @Inject constructor or an @Provides-annotated method.

Dagger会自动为目标类和依赖项类生成工厂代码 ->

```java
public final class UserLocalDataSource_Factory implements Factory<UserLocalDataSource> {
  @Override
  public UserLocalDataSource get() {
    return newInstance();
  }

  public static UserLocalDataSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserLocalDataSource newInstance() {
    return new UserLocalDataSource();
  }

  private static final class InstanceHolder {
    private static final UserLocalDataSource_Factory INSTANCE = new UserLocalDataSource_Factory();
  }
}
```

由于依赖项的构造是无参数的，所以我们看到的UserLocalDataSource的工厂代码与《1、@Inject标注无参构造》中UserRepository的工厂代码十分相似。

```java
public final class UserRepository_Factory implements Factory<UserRepository> {
    private final Provider<UserLocalDataSource> localDataSourceProvider;

    public UserRepository_Factory(Provider<UserLocalDataSource> localDataSourceProvider) {
        this.localDataSourceProvider = localDataSourceProvider;
    }

    @Override
    public UserRepository get() {
        return newInstance(localDataSourceProvider.get());
    }

    //create源码和前面无参的有点小差异，以Provider方式提供，不用太过纠结。
    public static UserRepository_Factory create(
            Provider<UserLocalDataSource> localDataSourceProvider) {
        return new UserRepository_Factory(localDataSourceProvider);
    }

    public static UserRepository newInstance(UserLocalDataSource localDataSource) {
        return new UserRepository(localDataSource);
    }
}
```

```kotlin
//方式1
UserRepository_Factory.newInstance(UserLocalDataSource())
//方式2
UserRepository_Factory.create(object : Provider<UserLocalDataSource> {
    override fun get(): UserLocalDataSource {
        return UserLocalDataSource()
    }
})
```

可见目标类的工厂类同样是提供了两种方式来创建目标类对象，一种是newInstance方案，一种是create-get方案。 newInstance的方式还好，传递一个UserLocalDataSource对象即可。
create的方式真是越写越复杂，由于Provider是Dagger提供的一个接口，create方法需要传递个Provider<UserLocalDataSource>类型的实例，我们还需自己创建一个，甚是麻烦。

还有一点可以留意下，目标类依赖了几个依赖项，在目标类中会生成几个Provider< 依赖项 >的成员。

###### 3、@Component容器

使用@Component标注一个接口，这个接口就是一个Dagger容器，Dagger容器可以很方便管理对象。上栗子：

```kotlin
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource
)

class UserLocalDataSource @Inject constructor()
```

```kotlin
@Component
interface ApplicationComponent {
    fun repository(): UserRepository
}
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)
        //从容器中拿对象
        val repo = DaggerApplicationComponent.create().repository()
        //依赖项的对象也可直接拿到
        val localDataSource = repo.localDataSource
    }
}
```

只需要使用容器就能很方便管理对象，不用手写啦~  再说手写谁还用Dagger直接new多快呢！看下生成的源码 ->

```java
//1、实现我们定义的容器接口
public final class DaggerApplicationComponent implements ApplicationComponent {
    //字段貌似没啥用？？？ 私有了，内部也未使用。  
    private final DaggerApplicationComponent applicationComponent = this;

    private DaggerApplicationComponent() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ApplicationComponent create() {
        return new Builder().build();
    }

    //2、实现接口中的方法
    @Override
    public UserRepository repository() {
        // 容器中的对象是new的方式创建，对象的依赖项也是new的方式创建  
        return new UserRepository(new UserLocalDataSource());
    }

    // 3、生成的容器代码使用了Builder模式
    public static final class Builder {
        private Builder() {
        }

        //build方法创建正真容器
        public ApplicationComponent build() {
            return new DaggerApplicationComponent();
        }
    }


}
```

```kotlin
val container1 = DaggerApplicationComponent.create()
val container2 = DaggerApplicationComponent.create()

val repo1 = container1.repository()
val repo2 = container1.repository()
```

可见 ->

- 每次获取容器时都会创建新的容器对象
- 每次获取目标对象时也会创建新的对象

这里先打个预防针，可以留意下容器生成类中无任何其他多余的成员属性字段了，到下文单例中可对比观察。

###### 4、Dagger容器单例

这个需要代码实现，通常我们可以把容器放到Application#onCreate中进行初始化，提供一个容器实例，以达到Dagger容器单例的目的。

```kotlin
class MyApplication : Application() {
    private val applicationComponent by lazy {
        DaggerApplicationComponent.create()
    }
    override fun onCreate() {
        super.onCreate()
    }
    fun getDaggerContainer(): ApplicationComponent = applicationComponent
}
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        println("daggerContainer:$daggerContainer")

    }
}
```

###### 5、容器内对象单例

容器管理的对象的生成默认都是非单例的，Dagger容器提供了相应的注解@Singleton可改变代码的生成方式，让生成的目标对象为单例，使用时只需要注意一点即可：
@Singleton注解必须同时标注Dagger容器与目标类上,否则注解不起作用，Dagger容器不会更改生成代码的方式。

```kotlin
@Singleton
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}
```
```kotlin
@Singleton
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource
)
class UserLocalDataSource @Inject constructor()
```

生成源码->

```java
public final class DaggerApplicationComponent implements ApplicationComponent {
  // 字段用不管，貌似没用。  
  private final DaggerApplicationComponent applicationComponent = this;
  // 1、定义个Provider<单例类> 作为成员变量
  private Provider<UserRepository> userRepositoryProvider;
  // 2、构造中产生单例
  private DaggerApplicationComponent() {initialize();}

  public static Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize() {
    // 等号右面就是Provider<UserRepository>实现类，这个实现类通过DoubleCheck工具类处理，每次get都是同一个实例。
    this.userRepositoryProvider = DoubleCheck.provider(UserRepository_Factory.create(UserLocalDataSource_Factory.create()));
  }

  // 3、获取目标类不再new，而是从单例工具处理过的类中去取。
  @Override
  public UserRepository repository() {
    return userRepositoryProvider.get();
  }

  public static final class Builder {
    private Builder() {
    }

    public ApplicationComponent build() {
      return new DaggerApplicationComponent();
    }
  }
}
```
生成的代码逻辑也很简单，在DaggerApplicationComponent类的构造中使用Dagger提供的一个工具类DoubleCheck做了单例处理，使repository()始终返回同一个对象。

接下来看一个简单的变形，把单例标签加给依赖项，目标对象的标签去除 ->

```kotlin
@Singleton
@Component
interface ApplicationComponent {
    fun repository(): UserRepository
}
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource
)
// 单例加给依赖项
@Singleton
class UserLocalDataSource @Inject constructor()
```

生成源码->

```java

public final class DaggerApplicationComponent implements ApplicationComponent {
  private final DaggerApplicationComponent applicationComponent = this;

  // 1、成员变成了Provider<UserLocalDataSource>
  private Provider<UserLocalDataSource> userLocalDataSourceProvider;

  private DaggerApplicationComponent() {initialize();}

  public static Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize() {
    this.userLocalDataSourceProvider = DoubleCheck.provider(UserLocalDataSource_Factory.create());
  }

  // 2、repository方法变成了默认new方式
  @Override
  public UserRepository repository() {
    return new UserRepository(userLocalDataSourceProvider.get());
  }

  public static final class Builder {
    private Builder() {
    }

    public ApplicationComponent build() {
      return new DaggerApplicationComponent();
    }
  }
}
```

通过两个栗子可知，添加作用域标签标明类后，这个类的单例在生成容器实现类时就生成了，其他生成类的源代码不会收到影响。此时我们可以大胆预言，假如目标类与依赖项都标注
作用域标签，生成的容器实现类中应该有两个成员Provider< 目标类 >和Provider< 依赖项 > ,源码就不给出了，可自行查看验证下~

总结，容器中存在多少单例对象，在容器生成类中就会有多少个Provider< 单例注解类 >类型的成员变量。

###### 6、自定义作用域

```java
//@Singleton源码
@Scope
@Documented
@Retention(RUNTIME)
public @interface Singleton {}

//@Scope源码 ->
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
@Documented
public @interface Scope {}
```
可以发现Scope为Dagger提供的一个作用域标签类，这个注解可以标注注解。我们完全可以不用@Singleton而定义个自己的标签：

```kotlin
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class MakeSingleTon
```

```kotlin
@MakeSingleTon
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}
```

###### 7、@Inject注解字段

这个可实现类似ButterKnife的效果，直接上使用,给Activity注入一个presenter字段~

```kotlin
@Component
interface ApplicationComponent {
     //定义个注入方法，表示往DaggerBasicActivity注入字段
     fun inject(activity: DaggerBasicActivity)
}
```
```kotlin
//注入字段所属的实体类型
class LoginPresenter @Inject constructor()
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {
    @Inject
    lateinit var loginPresenter: LoginPresenter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val daggerContainer = (application as MyApplication).getDaggerContainer()
        super.onCreate(savedInstanceState)
        //字段注入
        daggerContainer.inject(this)
        setContentView(R.layout.activity_dagger_basic)
        println("使用字段：$loginPresenter")
        //System.out: 使用字段：com.example.di.dagger_basic.repository.LoginPresenter@4bb080e
    }
}
```
需要留意几点

字段不能是private类型的否则报错Dagger does not support injection into private fields

有多个类要注入字段时，不能共用同一个注入方法，也即方法中的类型不能是基类，要是具体的类型，否则注入字段失败，如：
```kotlin
@Component
interface ApplicationComponent {
     fun inject(activity: Activity)
}
// 心想：修改参数为Activity这样所有的activity都能用这个方法进行字段注入了，大错特错，这样会注入失败：上线栗子改成这样会报错：
//kotlin.UninitializedPropertyAccessException: lateinit property loginPresenter has not been initialized
//可见注入未成功，延迟初始化的属性也未得到赋值，而报错。
```

字段注入的背后原理？？？

首先看LoginPresenter的工厂生成类，这个最简单，经过前面的探究我们想必已经知道了这个LoginPresenter工厂类的代码结构，一个newInsta方法，一个create-get方法：

```java
public final class LoginPresenter_Factory implements Factory<LoginPresenter> {
  @Override
  public LoginPresenter get() {
    return newInstance();
  }

  public static LoginPresenter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LoginPresenter newInstance() {
    return new LoginPresenter();
  }

  private static final class InstanceHolder {
    private static final LoginPresenter_Factory INSTANCE = new LoginPresenter_Factory();
  }
}
```

这里没啥看的，看下容器的生成代码->

```java
public final class DaggerApplicationComponent implements ApplicationComponent {
  private final DaggerApplicationComponent applicationComponent = this;

  private DaggerApplicationComponent() {}

  public static Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }
  
  @Override
  public void inject(DaggerBasicActivity activity) {
    injectDaggerBasicActivity(activity);
  }
  // 核心之处：可以看到这里的方法名，调用类名的命名很有规律的。
  private DaggerBasicActivity injectDaggerBasicActivity(DaggerBasicActivity instance) {
    DaggerBasicActivity_MembersInjector.injectLoginPresenter(instance, new LoginPresenter());
    return instance;
  }

  public static final class Builder {
    private Builder() {
    }

    public ApplicationComponent build() {
      return new DaggerApplicationComponent();
    }
  }
}
```
容器的实现类代码我们也是非常熟悉了，这里也没啥大差别。在接口实现类中实现接口方法，方法内调用了DaggerBasicActivity_MembersInjector#injectLoginPresenter
那么DaggerBasicActivity_MembersInjector这个类是怎样来的呢？？？

其实Dagger在编译器会扫描@Inject注解，当发现某个activity的字段使用了@Inject注解时，会自动生成一个XXXActivity_MembersInjector类。这点我们可以手动去除字段
上的注解来验证，去除后Dagger不会生成XXXActivity_MembersInjector类。看下源码：

```java
public final class DaggerBasicActivity_MembersInjector implements MembersInjector<DaggerBasicActivity> {
  // 2、至于为啥LoginPresenter的构造也要加注解，目前未搞清楚，我猜大概是可能会用到吧？？？
  private final Provider<LoginPresenter> loginPresenterProvider;

  public DaggerBasicActivity_MembersInjector(Provider<LoginPresenter> loginPresenterProvider) {
    this.loginPresenterProvider = loginPresenterProvider;
  }

  public static MembersInjector<DaggerBasicActivity> create(
      Provider<LoginPresenter> loginPresenterProvider) {
    return new DaggerBasicActivity_MembersInjector(loginPresenterProvider);
  }

  @Override
  public void injectMembers(DaggerBasicActivity instance) {
    injectLoginPresenter(instance, loginPresenterProvider.get());
  }
  
  @InjectedFieldSignature("com.example.di.dagger_basic.DaggerBasicActivity.loginPresenter")
  public static void injectLoginPresenter(DaggerBasicActivity instance, LoginPresenter loginPresenter) {
        //1、容器中调用注入方法时会走到这里。这里会给指定的activity的字段赋值。
        instance.loginPresenter = loginPresenter;
        // 知道为啥Dagger操作的字段不能是私有的了吧，因为这里就是普通的对象赋值，而不是通过反射操作的。
  }
}
```

总结：当我们在activity中调用容器的注入方法时，这时会把activity的实例传递过去，然后在Dagger容器中自动生成或根据已有工厂类获取字段的实例，自动赋值。

收获：不能够通过构造注入的字段我们可使用Inject方式注入，这种其实就是setter注入。

# Factory Provider Lazy


[参考](https://developer.android.google.cn/training/dependency-injection/dagger-basics)





