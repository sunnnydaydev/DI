# Dagger基础

# 前言

###### 1、依赖引入

使用Dagger时需要引入Dagger依赖，在app/build.gradle 添加下依赖：

```groovy

    apply plugin: 'kotlin-kapt'

    dependencies {
        implementation 'com.google.dagger:dagger:2.38.1'
        kapt 'com.google.dagger:dagger-compiler:2.38.1'
    }
```
###### 2、要使用的类

- UserRepository
- UserLocalDataSource
- UserRemoteDataSource

本文主要以这三个类为例子，三者存在依赖关系：UserRepository依赖UserLocalDataSource和UserRemoteDataSource，代码表示如下：

```kotlin
/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserLocalDataSource
class UserRemoteDataSource

class UserRepository(
     val localDataSource: UserLocalDataSource,
     val remoteDataSource: UserRemoteDataSource
)
```

# 使用Dagger进行自动依赖注入

###### 1、如何让Dagger自动生成实例

这里我们会碰到第一个要使用的注解@Inject。我们使用@Inject注解构造函数即可。

```kotlin
/**
 * Create by SunnyDay /07/06 21:28:16
 */
class UserLocalDataSource @Inject constructor()
class UserRemoteDataSource @Inject constructor()

class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
)

```
UserRemoteDataSource与UserLocalDataSource类似都是一个类，无任何字段成员，这里就选取一个看下效果。

注解后点击Idea->Build->Make Project这是编译器就会自动生成代码，代码在app/build/generated/source/kapt/debug/package下

```java
public final class UserRemoteDataSource_Factory implements Factory<UserRemoteDataSource> {
  //方式1：Factory接口提供的get方法获取
  @Override
  public UserRemoteDataSource get() {
    return newInstance();
  }
  // 提供create方法来创建自身对象
  public static UserRemoteDataSource_Factory create() {
    return InstanceHolder.INSTANCE;
  }
  //方式2：静态newInstance方法调用，直接获取被生成类实例。
  public static UserRemoteDataSource newInstance() {
    return new UserRemoteDataSource();
  }

  private static final class InstanceHolder {
    private static final UserRemoteDataSource_Factory INSTANCE = new UserRemoteDataSource_Factory();
  }
}
```

```java
public interface Factory<T> extends Provider<T> { }
public interface Provider<T> {
    T get();
}
```

可见，仅仅加了一个注解后Dagger帮助我们自动生成了代码。观看自动生成的代码我们会发现一些信息：

- Dagger会为目标类生成工厂类，工厂类命名有规则(目标类名_Factory)。 工厂类实现Factory<目标类>接口
- Factory是Dagger库提供的一个接口,其继承Provider接口，Provider接口中定义了get方法。
- 工厂类提供了两种方式创建目标类实例（get、newInstance，生成的对象非单例）
- 工厂类提供create方法来创建自身对象（生成的对象非单例）


好了，我们来看看UserRepository的生成类吧：

```java
public final class UserRepository_Factory implements Factory<UserRepository> {
  private final Provider<UserLocalDataSource> localDataSourceProvider;

  private final Provider<UserRemoteDataSource> remoteDataSourceProvider;

  public UserRepository_Factory(Provider<UserLocalDataSource> localDataSourceProvider,
      Provider<UserRemoteDataSource> remoteDataSourceProvider) {
    this.localDataSourceProvider = localDataSourceProvider;
    this.remoteDataSourceProvider = remoteDataSourceProvider;
  }

  @Override
  public UserRepository get() {
    return newInstance(localDataSourceProvider.get(), remoteDataSourceProvider.get());
  }

  public static UserRepository_Factory create(Provider<UserLocalDataSource> localDataSourceProvider,
      Provider<UserRemoteDataSource> remoteDataSourceProvider) {
    return new UserRepository_Factory(localDataSourceProvider, remoteDataSourceProvider);
  }

  public static UserRepository newInstance(UserLocalDataSource localDataSource,
      UserRemoteDataSource remoteDataSource) {
    return new UserRepository(localDataSource, remoteDataSource);
  }
}

```

看过UserRemoteDataSource的工厂类后这里就很容易理解了：

- 同样也是生成一个工厂类，实现Factory接口。
- 目标类通过构造函数注入对对象，这里就通过构造函数注入目标类的工厂类对象
- 同样通过两种方式提供目标类的实例（get、newInstance）
- 同样通过create方法获取自身的工厂类实例

###### 2、通过容器管理依赖项

给构造函数添加@Inject注解之后Dagger便为我们生成了对象，此时我想获取UserRepository对象该如何办呢？通过上述的源码我们知道我们有如下方式

- 通过工厂类的静态方法newInstance
- 通过工厂类的方法get

这两种方法都需要我们手写一些代码，其实针对无参数的对象来说还好些，如上的UserRepository就繁琐了，还要我们手写依赖项对象的创建。

这对我们来说好像挺繁琐的，接下来看看如何通过容器管理来简化这个操作的,容器的创建很简单，定义个接口，然后使用@Component注解标注下即可:

```kotlin
@Component
interface ApplicationComponent {}
```
接下来看看如何使用容器来管理依赖的：

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
```

- DaggerApplicationComponent是ApplicationComponent接口的实现类。这也是系统生成的，并且系统提供了如何获取DaggerApplicationComponent实例的方法
- 生成类的名字也是有规律的：Dagger+接口名
- 生成类采取Build模式获取

这看起来貌似简单多啦😁我们可看下容器实现类的具体就看源码：

```java
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

- 通过Build模式来创建容器对象。因此直接DaggerApplicationComponent#create 或者 DaggerApplicationComponent#Builder#builder都能获取到容器对象。
- 容器实现了接口的方法，并且通过new方式提供对象
- 容器管理的对象默认情况下非单例的，默认情况下被管理的对象都是new出来的。 容器本身也是非单例的。Build模式创建，一看就知道。

# Dagger容器内对象的单例

容器管理的对象默认情况下是非单例的，想要让容器管理的对象单例可以使用@Singleton注解。如何去做呢？我们只需做到两步：

###### 1、给容器添加@Singleton

```kotlin
@Singleton
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
}
```

###### 2、给容器管理的对象添加@Singleton

```kotlin
@Singleton
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
)
```

这样UserRepository的对象就是单例的了。 但是注意添加@Singleton注解后对容器本身是没作用的，并不会使容器单例。但是可以把容器管理的对象单例。

接下来还是看下生成的代码：

```java

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
我们可以知道

- 添加@Singleton注解后对DaggerApplicationComponent本身对象的创建未作任何变化。
- 接口中定义的方法获取相应对象时方式改变了，使用了DCL方式创建对象。
- 对于目标类添加@Singleton注解与未添加@Singleton注解时生成的代码是一致的。单例的处理是在容器类中进行的。


注意📢单例也是建立在Dagger容器的管理下的。使用容器管理后我们不要随便创建相应的对象了，否则就打破单例了。 @Singleton注解并不会使容器本身单例，那么我们如何使容器单例呢？通常我们首先想到的就是采用单例模式，但这里有一种更加
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

# 自定义作用域标签

@Singleton 是Dagger默认提供的作用域标签，这个标签的设计初衷是将某个对象的生命周期限定为整个应用的生命周期内。当我们期望某个对象的生命周期为Activity的生命周期内、Fragment生命周期内
该如何做呢？这时就需要自定义作用域标签了，如何自定义呢？很简单使用Dagger提供的@Scope标签即可：

```kotlin
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope
```
- 核心是Dagger提供的@Scope注解，使用这个注解标记我们自定义的注解即可。
- 使用也很简单与@Singleton一样，给容器组件添加这个注解，然后给容器直接或者间接管理的目标类添加这个注解即可。






