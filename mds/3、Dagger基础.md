# Dagger基础

###### 1、依赖引入

app/build.gradle 添加下依赖

```groovy

    apply plugin: 'kotlin-kapt'

    dependencies {
        implementation 'com.google.dagger:dagger:2.38.1'
        kapt 'com.google.dagger:dagger-compiler:2.38.1'
    }
```
###### 2、要使用的类

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/userrepo.png)

本文主要以这三个类为例子：

- UserRepository是数据仓库管理类，负责管理UserLocalDataSource和UserRemoteDataSource

- UserLocalDataSource负责管理本地数据

- UserRemoteDataSource负责管理远程数据

三者可构成依赖关系，UserRepository依赖UserLocalDataSource和UserRemoteDataSource，用代码表示如下：

```kotlin
/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository(
     val localDataSource: UserLocalDataSource,
     val remoteDataSource: UserRemoteDataSource
)
class UserLocalDataSource
class UserRemoteDataSource
```

###### 2、如何自动生成实例

  (1) @Inject注解无参构造

```kotlin
/**
 * Create by SunnyDay /07/06 21:28:16
 */
class UserRemoteDataSource @Inject constructor()
```
- UserRemoteDataSource与UserLocalDataSource类似都是一个类，无任何字段成员，这里就选取一个看下效果。
- 注解后点击Idea->Build->Make Project这是编译器就会自动生成代码，代码在app/build/generated/source/kapt/debug/package下

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class UserRemoteDataSource_Factory implements Factory<UserRemoteDataSource> {
  //方式2：生成类对象.get()
  @Override
  public UserRemoteDataSource get() {
    return newInstance();
  }
  //生成类的获取是单栗的。
  public static UserRemoteDataSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }
  //方式1：静态调用，直接获取被生成类实例。
  public static UserRemoteDataSource newInstance() {
    return new UserRemoteDataSource();
  }

  private static final class InstanceHolder {
    private static final UserRemoteDataSource_Factory INSTANCE = new UserRemoteDataSource_Factory();
  }
}
```
可见，仅仅加了一个注解后Dagger帮助我们自动生成了代码。观看自动生成的代码我们会发现一些信息。


- 生成类为工厂类，类名有规则：被生成类名_Factory
- 生成类提供了两种方式创建被生成类实例。
- 默认情况下被生成类实例非单例。

```kotlin
        // 两种方式获取被生成类对象
        val userRemoteDataSource1:UserRemoteDataSource = UserRemoteDataSource_Factory.newInstance()
        val userRemoteDataSource2:UserRemoteDataSource  = UserRemoteDataSource_Factory.create().get()
        Log.d(tag,"userRemoteDataSource1:$userRemoteDataSource1")
        Log.d(tag,"userRemoteDataSource2:$userRemoteDataSource2")
        /**
           log:
           D/MainActivity: userRemoteDataSource1:com.example.stu_dagger.repo.UserRemoteDataSource@67f5a77
           D/MainActivity: userRemoteDataSource2:com.example.stu_dagger.repo.UserRemoteDataSource@d3f6e4
         */
```
（2）@Inject注解有参构造

```kotlin
/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository @Inject constructor(
     val localDataSource: UserLocalDataSource,
     val remoteDataSource: UserRemoteDataSource
)
```

上面的UserRemoteDataSource和UserLocalDataSource都是无参构造，接下来看看有参构造注解后生成代码后有啥不同：

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})

//Factory继承了Provider
public final class UserRepository_Factory implements Factory<UserRepository> {
  // 定义了两个依赖。   
  private final Provider<UserLocalDataSource> localDataSourceProvider;
  private final Provider<UserRemoteDataSource> remoteDataSourceProvider;
  
  public UserRepository_Factory(Provider<UserLocalDataSource> localDataSourceProvider,
      Provider<UserRemoteDataSource> remoteDataSourceProvider) {
    this.localDataSourceProvider = localDataSourceProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }
  // 方式2：通过被生成类对象.get()获取。被生成类的构造需要特定参数。
  @Override
  public UserRepository get() {
    return newInstance(localDataSourceProvider.get(), remoteDataSourceProvider.get());
  }
  
  public static UserRepository_Factory create(Provider<UserLocalDataSource> localDataSourceProvider,
      Provider<UserRemoteDataSource> remoteDataSourceProvider) {
    return new UserRepository_Factory(localDataSourceProvider, remoteDataSourceProvider);
  }
  // 方式1：通过静态方法来直接获取被生成类实例，这要求用户手动传需要的参数。
  public static UserRepository newInstance(UserLocalDataSource localDataSource,
      UserRemoteDataSource remoteDataSource) {
    return new UserRepository(localDataSource, remoteDataSource);
  }
}
```
大致瞄一眼和上述的UserRemoteDataSource的生成类的结构类似，区别就是UserRepository构造有无参数问题。

直接看下如何通过如上两种方式获取UserRepository实例的吧！

```kotlin
        //方式1
        val userRepository1:UserRepository = UserRepository_Factory.newInstance(
            UserLocalDataSource(),
            UserRemoteDataSource()
        )
       //方式2
        val userRepository2: UserRepository = UserRepository_Factory.create(
            UserLocalDataSource_Factory.create(),
            UserRemoteDataSource_Factory.create()
        ).get()
        Log.d(tag,"userRepository1:$userRepository1")
        Log.d(tag,"userRepository2:$userRepository2")
        /**
         * D/MainActivity: userRepository1:com.example.stu_dagger.repo.UserRepository@d4b9d50
         * D/MainActivity: userRepository2:com.example.stu_dagger.repo.UserRepository@f008b49
         * */

```
可见同样提供了两种方案来获取被生成类的实例。方式1还好说依赖我们手动new 参数传递。方式2就不那么友好了，需要被依赖的类也要使用@Inject注解下。
否则我们都无法传递Provider< T > 类型的参数。

###### 3、通过容器管理

我们先总结下上述获取对象的方式，想必心里都已经有答案了"搞得有点复杂"写了一堆代码才能获取对象，这些工作要我做要你Dagger有毛用？？？

哈哈哈，一步一步来嘛~ 上述手动写了一遍虽然繁琐但是使我们大致明白了生成类的结构，创建方式。

其实Dagger还提供了容器管理。还是老样子，再看一个例子。

（1）容器创建&依赖管理
```kotlin
@Component
interface ApplicationComponent {
}
```
容器的创建很简单，定义个接口，然后使用@Component注解标注下即可。 接下来看看如何使用容器来管理依赖的：

```kotlin
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
}
```
好了，完事。这样容器就接手了具有依赖关系类的实例创建工作。 接下类看看如何使用了：

```kotlin
        // 获取UserRepository实例
        val userRepository:UserRepository = DaggerApplicationComponent.create().getUserRepository()
        // 获取UserRepository的依赖项
            userRepository.localDataSource
            userRepository.remoteDataSource
```
哈哈哈调用是不是贼简单。这里先知道：

- DaggerApplicationComponent是ApplicationComponent接口的实现类即可。这也是系统生成的，并且系统提供 了如何获取DaggerApplicationComponent实例的方法
- 生成类的名字也是有规律的：Dagger+接口名
- 生成类采取Build模式获取

具体就要看源码喽~

（2）容器生成类是怎样的？
```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerApplicationComponent implements ApplicationComponent {
  private final DaggerApplicationComponent applicationComponent = this;

  private DaggerApplicationComponent() {


  }

  public static Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }

  // 接口实现方法，并且自动实现。每次调用都会new对象。
  @Override
  public UserRepository getUserRepository() {
    return new UserRepository(new UserLocalDataSource(), new UserRemoteDataSource());
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

- 通过Build模式来创建生成类实例。因此直接DaggerApplicationComponent#create 或者 DaggerApplicationComponent#Builder#builder都能获取到生成类对象。
- 生成类中自动生成接口实现方法。
- 很好理解容器就是管理具有依赖关系的对象创建的，只要吧具有依赖关系的对象放入容器中容器就自动管理。
- 不过容器管理的对象默认情况下非单例的，默认情况下被管理的对象都是new出来的。
- 容器本身也是非单例的。Build模式创建，一看就知道。

Dagger容器统一管理图解

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/DaggerContainer.png)


###### 4、容器内对象的单例

容器管理的对象默认情况下是非单例的，想要让提供的对象单例可以使用@Singleton注解。

```kotlin
@Singleton
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
}

@Singleton
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
)
```
代码验证：

```kotlin
        // DaggerApplicationComponent
        val container1 = DaggerApplicationComponent.create()
        val container2 = DaggerApplicationComponent.create()
        //加上@Singleton后验证下DaggerApplicationComponent是否单例
        Log.d(tag,"container1:$container1")
        Log.d(tag,"container2:$container2")
        
        /**
        contain对象不同：
        D/MainActivity: container1:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
        D/MainActivity: container2:com.example.stu_dagger.components.DaggerApplicationComponent@511216f
         */

        val userRepository3:UserRepository = container1.getUserRepository()
        val userRepository4:UserRepository = container1.getUserRepository()
        //加上@Singleton后验证下UserRepository获取是否单例。
        Log.d(tag,"userRepository3:$userRepository3")
        Log.d(tag,"userRepository4:$userRepository3")
        /**
        userRepository对象相同
        D/MainActivity: userRepository3:com.example.stu_dagger.repo.UserRepository@b553e7c
        D/MainActivity: userRepository3:com.example.stu_dagger.repo.UserRepository@b553e7c
         * */
```
可见添加@Singleton注解后对容器本身是没作用的，并不会使容器单例。但是可以把容器管理的对象单例。

通过代码和log可以得出上述结论。接下来还是看下生成的代码再印证下：

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerApplicationComponent implements ApplicationComponent {
  private final DaggerApplicationComponent applicationComponent = this;

  private Provider<UserRepository> userRepositoryProvider;

  private DaggerApplicationComponent() {
    // 与不加单例的区别之处，这里多了个方法调用。
    initialize();

  }

  public static Builder builder() {
    return new Builder();
  }

  public static ApplicationComponent create() {
    return new Builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize() {
      // 采用dcl方式实现单例
    this.userRepositoryProvider = DoubleCheck.provider(UserRepository_Factory.create(UserLocalDataSource_Factory.create(), UserRemoteDataSource_Factory.create()));
  }

  @Override
  public UserRepository getUserRepository() {
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
可见：

- 添加@Singleton注解后对DaggerApplicationComponent本身对象的创建未做任何变化。
- 接口中定义的方法获取相应对象时方式改变了，使用了单例模式的DCL方案。
- 添加@Singleton注解后与未添加注解时生成的目标类代码一致。单例的处理是在容器类中处理的。

去除UserRepository的@Singleton，给 UserRemoteDataSource添加@Singleton,看下面的变化点可印证："添加@Singleton注解后与未添加注解时生成的目标类代码一致。单例的处理是在容器类中处理的。"

```java
@DaggerGenerated
@SuppressWarnings({
        "unchecked",
        "rawtypes"
})
public final class DaggerApplicationComponent implements ApplicationComponent {
    private final DaggerApplicationComponent applicationComponent = this;

    private Provider<UserRemoteDataSource> userRemoteDataSourceProvider;

    private DaggerApplicationComponent() {

        initialize();

    }

    public static Builder builder() {
        return new Builder();
    }

    public static ApplicationComponent create() {
        return new Builder().build();
    }

    @SuppressWarnings("unchecked")
    private void initialize() {
        // 变化点
        this.userRemoteDataSourceProvider = DoubleCheck.provider(UserRemoteDataSource_Factory.create());
    }

    @Override
    public UserRepository getUserRepository() {
        return new UserRepository(new UserLocalDataSource(), userRemoteDataSourceProvider.get());
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

注意：单例也是建立在Dagger容器的管理下的。使用容器管理后我们不要随便创建相应的对象了，否则就打破单例了。

###### 5、单例补充

（1）容器的单例

上面的例子我们或许也发现了@Singleton注解并不会使容器本身单例，那么我们如何使容器单例呢？通常我们首先想到的就是采用单例模式，但这里有一种更加
快捷方便的方法，结合Application：

```kotlin
/**
 * Create by SunnyDay /07/10 22:00:08
 */
class MyApplication :Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
    override fun onCreate() {
        super.onCreate()
    }
}
```

```kotlin
        // DaggerApplicationComponent
        val container3 = (application as MyApplication).appComponent
        val container4 = (application as MyApplication).appComponent
        //加上@Singleton后验证下DaggerApplicationComponent是否单例
        Log.d(tag,"container3:$container3")
        Log.d(tag,"container4:$container4")
        /**
        D/MainActivity: container3:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
        D/MainActivity: container3:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
         * */
```

（2）自定义容器内对象的生命周期

我们可以使用作用域注解将某个对象的生命周期限定为其组件的生命周期。如上的@Singleton是系统提供的注解，当然我们也可以创建并使用自定义作用域注解

```kotlin
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class MyCustomScope
```
定义十分简单：核心是系统的@Scope注解，使用这个注解标记我们自定义注解即可。
使用也很简单：给容器组件添加这个注解，然后给容器直接或者间接管理的目标类添加这个注解即可。




