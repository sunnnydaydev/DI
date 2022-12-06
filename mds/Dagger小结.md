# @Inject注解

这里只谈论@Inject注解，不谈论@binds，@provider！！！

###### 1、@Inject标注无参构造

```kotlin
class UserRepository @Inject constructor()
```

Dagger会自动为这个类生成工厂代码 ->

```java

@DaggerGenerated
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

工厂类提供了两种方案来创建UserRepository目标类对象实例：

```kotlin
//方式1：通过newInstance方法
val userRepository1 = UserRepository_Factory.newInstance()
//方式2：通过create#get方法
val userRepository2 = UserRepository_Factory.create().get()
```

###### 2、@Inject标注有参构造

```kotlin
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
)

//依赖项
class UserLocalDataSource @Inject constructor()
```

构造中的参数就是依赖项，@Inject标注的构造有依赖项时其依赖项也要使用@Inject标注，否则dagger编译报错：

UserLocalDataSource cannot be provided without an @Inject constructor or an @Provides-annotated
method.

Dagger会自动为这个类生成工厂代码 ->

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

    //create源码和前面无参的有点小差异，不用太过纠结。
    public static UserRepository_Factory create(
            Provider<UserLocalDataSource> localDataSourceProvider) {
        return new UserRepository_Factory(localDataSourceProvider);
    }

    public static UserRepository newInstance(UserLocalDataSource localDataSource) {
        return new UserRepository(localDataSource);
    }
}
```

工厂类提供了两种方案来创建UserRepository目标类对象实例：

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

同样也是可通过两种方式来获取目标类的实例。newInstance的方式还好，传递一个UserLocalDataSource对象即可。create的方式真是越写越复杂，由于Provider
是Dagger提供的一个接口，create方法需要传递个Provider<UserLocalDataSource>类型的实例，我们还需自己创建一个，甚是麻烦。

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

只需要使用容器就能很方便管理对象，不用手写啦~  再说手写谁还用Dagger直接new多块呢！

看下生成的源码

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

todo 整理

[参考](https://developer.android.google.cn/training/dependency-injection/dagger-basics)





