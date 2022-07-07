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


- UserRepository是数据仓库管理类，负责管理UserLocalDataSource和UserRemoteDataSource

- UserLocalDataSource负责管理本地数据

- UserRemoteDataSource负责管理远程数据

三者可构成依赖关系，UserRepository依赖UserLocalDataSource和UserRemoteDataSource，用代码表示如下：

```kotlin
/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
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
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
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

（1）创建一个容器
```kotlin
@Component
interface ApplicationComponent {
}
```
很简单，定义个接口，然后使用@Component注解标注下即可。生成代码先不看了，和下面ð一起看喽！

（2）让容器来管理依赖

###### 4、单例

