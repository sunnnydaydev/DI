# Dagger常见的注解

在Dagger基础中认识了几个注解：@inject、@Component、@Singleton，本章节就总结下其他的常用的注解。

- @Inject字段注入
- @Module 模块的概念
- @Subcomponent 子组件

###### 1、使用@Inject进行字段注入

安卓中，Activity的创建不是由开发者控制的，由系统的AMS负责创建、管理。这时我们便不能通过构造方法进行字段注入了，恰巧Dagger提供了相应的注入
方式。接下来看下Activity中如何自动注入LoginPresenter的。

依赖关系很简单，Activity中持有loginPresenter字段。接下来看看Dagger是如何实现自动注入的。

（1）容器中定义个注入的方法。

```kotlin
/**
 * Create by SunnyDay /07/07 21:32:57
 */
@Singleton
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
    //核心api，为目标类的实例的字段注入值。
    fun inject(activity:MainActivity)
}
```
（2）使用

```kotlin
/**
 * Create by SunnyDay /07/11 20:35:29
 */
class LoginPresenter @Inject constructor(){}
```

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        Log.d(tag,"loginPresent:$loginPresent")
    }
}
```
可见十分简单：定义个注入方法->在activity onCreate方法中注入下 ->定义的字段就有值了。

注意点：

- 字段的访问权限不能为private的。否则Dagger报错。
- 若有多个类不要注入时需要提供多个注入方法，不能共用一个注入方法。如SignUpActivity也需要注入字段，则我们应在容器中再定义个注入方法，传入的参数类型为SignUpActivity。
- 字段所属的类的构造上别忘了加@Inject。

了解过XUtils或者APT的大概就可很快明白Dagger字段注入的原理。很明显Dagger的处理方式不是类似XUtils而是使用的APT。核心原理就是setter方式注入。 我们来看下相关的生成类：

- LoginPresenter的生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
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
很简单，工厂方式创建LoginPresenter的对象，然后暴露get方法。上一节我们已经见过无参构造添加@Inject的，这种生成类是最简单的。

- Activity相关的生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})

// 泛型，类型就是我们的MainActivity类型。
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {

  private final Provider<LoginPresenter> loginPresentProvider;


  public MainActivity_MembersInjector(Provider<LoginPresenter> loginPresentProvider) {
    this.loginPresentProvider = loginPresentProvider;
  }
  
  public static MembersInjector<MainActivity> create(
      Provider<LoginPresenter> loginPresentProvider) {
    return new MainActivity_MembersInjector(loginPresentProvider);
  }

  //自身实例调用。貌似没使用到。
  @Override
  public void injectMembers(MainActivity instance) {
    injectLoginPresent(instance, loginPresentProvider.get());
  }

  //静态方法直接给目标对象的成员设置字段值。
  @InjectedFieldSignature("com.example.stu_dagger.MainActivity.loginPresent")
  public static void injectLoginPresent(MainActivity instance, LoginPresenter loginPresent) {
    instance.loginPresent = loginPresent;
  }
}
```
由于Activity的字段添加了@Inject注解，因此系统也为Activity生成了一个类。

- 容器生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerApplicationComponent implements ApplicationComponent {
    ...
    // 1、首先实现接口方法
  @Override
  public void inject(MainActivity activity) {
    injectMainActivity(activity);
  }
  //2、其次调用MainActivity_MembersInjector#injectLoginPresent对目标对象的字段进行注入。
  //   注入的值是直接new出来的。  
  private MainActivity injectMainActivity(MainActivity instance) {
    MainActivity_MembersInjector.injectLoginPresent(instance, new LoginPresenter());
    return instance;
  }

}
```

流程总结：

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/flow.png)

留个小疑问？

- MainActivity_MembersInjector生成类是不是有点鸡肋，注入动作在容器生成类中都可自动完成？
- MainActivity_MembersInjector只用了静态调用的方式，create的方案就没使用？

###### 2、Module相关

- @Provider：告知 Dagger 如何提供您的项目所不具备的类。
- @Binds：告知 Dagger 接口应采用哪种实现。


（1）@Provider

假如此时有一个"用户登录的需求"，登录的参数需要在LoginPresenter构造中传入：

```kotlin
/**
 * Create by SunnyDay /07/12 20:25:18
 */
interface ApiService {
    @GET("/")
    fun getLoginDataFromSever(): Call<ResponseBody>
}
```

```kotlin
/**
 * Create by SunnyDay /07/11 20:35:29
 */
class LoginPresenter @Inject constructor(private val service:ApiService){
    fun login(){
        service.getLoginDataFromSever().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("LoginPresenter", "onResponse")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("LoginPresenter", "onFailure")
            }
        })
    }
}
```

仔细一看我们或许会意识到，这里的参数有点特殊呢，还不能直接让系统new生成对象。retrofit有自己的创建方式。

首先看下这种方式跑起来会怎样？？？？

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        loginPresent.login()
    }
}

// ApiService cannot be provided without an @Provides-annotated method.
```

果然报错了，这里若是个普通的对象呢？？？ 既然有疑问我们就多跑几下：

```kotlin
/**
 * Create by SunnyDay /07/12 20:52:35
 */
class User constructor() {
    var name = ""
    var age = 18

    override fun toString(): String {
        return "[name：$name ,age:$age]"
    }
}
```
```kotlin
class LoginPresenter @Inject constructor(private val user: User){
    //private val service:ApiService
    fun login(){
//        service.getLoginDataFromSever().enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Log.i("LoginPresenter", "onResponse")
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.i("LoginPresenter", "onFailure")
//            }
//        })
        Log.i("LoginPresenter", "user:$user")
    }
}
```
编译一下发现：User cannot be provided without an @Inject constructor or an @Provides-annotated method.

有问题的需要给User构造添加@Inject注解或者使用@Provides方式提供实例。我们知道给User的构造添加@Inject注解是一定能够编译通过的，上述会打印
log：LoginPresenter: user:[name： ,age:18]。

既然无法给目标类的构造添加注解我们就看看@Provides的方案如何使用的。还是的retrofit栗子：

```kotlin
/**
 * @Provides要结合@Module使用，因此首先要创建Module，使用@Module标注，然后提供目标实例方法，使用@Provides标注。
 * */ 
@Module
class NetWorkModules {
    /**
     * 提供Retrofit实例
     * */
    @Provides
    fun provideLoginRetrofitService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .build()
            .create(ApiService::class.java)
    }
}
```

```kotlin
/**
 * 第二步就是向容器注册module，也即让容器接手管理。
 * 这样当容器发现他管理的依赖关系图中某个对象未通过@Inject注解时，就会取Module中查找module提供的api。
 * ps：若有多个module注册时直接逗号隔开即可。
 * */
@Singleton
@Component(modules = [NetWorkModules::class])
interface ApplicationComponent {
    fun getUserRepository():UserRepository

    fun inject(activity:MainActivity)
}
```

只需两步，注释是真的详细，开撸，再跑下->

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        loginPresent.login()
    }
}
//  I/LoginPresenter: onResponse
```

此时有一个需求来了，我需要可配置OkhttpClient，那provideLoginRetrofitService就需要接收一个参数了：

```kotlin
    @Provides
    fun provideLoginRetrofitService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(client)
            .build()
            .create(ApiService::class.java)

    }
```

不出意外这样跑起来肯定报错，因为Dagger容器又不知道client这个参数去那里找了，跑来看看:okhttp3.OkHttpClient cannot be provided without an @Inject constructor or an @Provides-annotated method.
看到这我们想必心里有答案了，报错和前面一样。两种方式可解决。

- 使用@Inject注解目标类的构造，这里若是普通类还行，但OkHttpClient还是不行，虽然可以通过new的方式创建，但是我们无法去源码中加注解。
- 使用@Provides提供一下即可。

```kotlin
@Module
class NetWorkModules {
    /**
     * 提供Retrofit实例
     * */
    @Provides
    fun provideLoginRetrofitService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(client)
            .build()
            .create(ApiService::class.java)

    }
    @Provides
    fun providerOkhttpClient():OkHttpClient{
        return OkHttpClient()
        // return OkHttpClient.Builder().build() // 这样也可
    }
}
```

这样就完美跑起来了。

（2）@Binds





