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

只需要使用容器就能很方便管理对象，不用手写啦~  再说手写谁还用Dagger直接new多块呢！看下生成的源码 ->

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

通过两个栗子可知，添加作用域标签标明类后，这个类的单例在生成容器实现类中就生成了，其他生成类的源代码不会收到影响。此时我们可以大胆预言，假如目标类与依赖项都标注
作用域标签，生成的容器实现类中应该有两个成员Provider< 目标类 >，Provider< 依赖项 > ,源码就不给出了，可自行查看验证下~

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

# Factory与Provider




[参考](https://developer.android.google.cn/training/dependency-injection/dagger-basics)





