# Dagger@Inject字段注入与Dagger#Module

- @Inject 进行字段注入
- @Module 模块的概念

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

###### 2、Module相关@Provider

- @Provider：告知 Dagger 如何提供您的项目所不具备的类。
- @Binds：告知 Dagger 接口应采用哪种实现。


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

###### 3、Module相关@Binds

首先看个例子，为MainActivity添加HomePresent来控制Home上的主要业务逻辑，简单模拟下：

```kotlin
/**
 * Create by SunnyDay /07/13 21:09:54
 */
interface HomePresenter {
    fun logOut()
}

class HomePresenterImpl :HomePresenter {
    override fun logOut() {
        println("logout")
    }
}
```

然后MainActivity我们作为字段使用：

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    
    //HomePresenterImpl cannot be provided without an @Inject constructor or an @Provides-annotated method.
    @Inject
    lateinit var homePresenter:HomePresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        loginPresent.login()
        // 使用对象
        homePresenter.logOut()
    }
}
```
如上，直接编译会报错需要给HomePresenterImpl类构造添加@Inject注解或者使用@Provides方式提供对象。

先不修改呢，其实在上述的基础上我们还能模拟出另一个bug：

```kotlin
    //HomePresenter cannot be provided without an @Provides-annotated method.
    @Inject
    lateinit var homePresenter:HomePresenter
```
可见使用接口（HomePresenter）时提示只能使用@Provides方式提供对象了，这里我们可以留意下这种细节。

此时我们好像对Dagger容器管理的对象有了一点收获： Dagger容器管理的对象或者管理的注入字段，提供实例的方式都一般有两种方式一种是目标类构造使用@Inject
注解，另外一种就是使用@Providers注解提供实例。

好了就按照@Provides方式进行修改：

```kotlin
/**
 * Create by SunnyDay /07/13 21:43:19
 */
@Module
class HomeModule {
    @Provides
    fun providerHomePresenter():HomePresenter{
        return HomePresenterImpl()
    }
}
```

向容器注册下：

```kotlin
@Component(modules = [NetWorkModules::class,HomeModule::class])
interface ApplicationComponent {

    fun getUserRepository():UserRepository

    fun inject(activity:MainActivity)
}
```
这样mainActivity中的homePresenter就有值了。

其实上述只是简单的模拟，打个log，想要更加贴切我们再深入模拟：

```kotlin
/**
 * Create by SunnyDay /07/13 21:11:45
 */
class HomePresenterImpl constructor(private val userService: ApiService) :HomePresenter {
    override fun logOut() {
        userService.logOutFromSever().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("HomePresenterImpl", "onResponse")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("HomePresenterImpl", "onFailure")
            }
        })
    }
}
```
给HomePresenterImpl构造添加一个参数。此时有了先前的经验我们知道这时要做两步：

（1）给HomePresenterImpl构造添加@Inject注解。由于此时我们HomePresenterImpl已经通过@Providers方式提供了所以这步不用做了。

（2）通过@Provider提供个ApiService。这个我们前面举例子举过，这里稍作修改。

```kotlin
@Module
class HomeModule {
    /**
     * 思考：
     * 
     * 1、HomePresenterImpl需要参数的，我们这里只能通过方法提供。因此给providerHomePresenter添加参数这种方式我们就熟了，
     * 前面添加OkhttpClient参数做过。因此仿照即可。
     * 2、ApiService貌似之前NetWorkModule创建过。NetWorkModule与HomeModule都归我们的ApplicationComponent管理，因此
     * 已经有的实力这里不用写重新创建了，dagger容器会自动找到NetWorkModule中提供的。
     */
    @Provides
    fun providerHomePresenter(api: ApiService):HomePresenter{
        return HomePresenterImpl(api)
    }
}
```
emmmm，跑一下真的跑起来了。

这里或许会产生个疑问，如下，MainActivity中定义的这个字段类型是LoginPresenter类型，接口的实现类可能有很多Dagger是如何区分的？？？
```kotlin
    @Inject
    lateinit var loginPresent:LoginPresenter
```

如下稍微模拟
```kotlin
@Module
class HomeModule {
    @Provides
    fun providerHomePresenter(api: ApiService):HomePresenter{
        val result = HomePresenterImpl(api)
        println("HomeModule:result1")
        return result
    }

    @Provides
    fun providerHomePresenter1(api: ApiService,s:String):HomePresenter{
        val result2 = HomePresenterImpl(api)
        println("HomeModule:result2")
        return result2
    }

    @Provides
    fun providerString():String{
        return "s"
    }

}
```
运行直接回出现编译提示：HomePresenter is bound multiple times。可以看到MainActivity中的HomePresenter字段被注入多次这是
不允许的。

倘若我们把providerHomePresenter1改为providerHomePresenter回如何？还是会报错的：Cannot have more than one binding method with the same name in a single module
这又是Dagger的规定，一个Module中只能有一个唯一的函数名。

注意上述的两个方法的返回值都是HomePresenter类型，而MainActivity中定义的字段类型也是。这也是Dagger的规定，Dagger是根据类的类型去找
给自己提供实例的方法的，不用管方法名、方法参数之类的。


经过一番尝试，又了解了很多东西，接下来回归正题，看看@Binds的使用场景吧！如何使用@Binds来替换@Providers。

梳理下之前的代码把：首先看下使用，HomePresenter字段为Dagger容器注入的。

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter

    //字段
    @Inject
    lateinit var homePresenter:HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注入
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        loginPresent.login()
        // 使用字段。
        homePresenter.logOut()
    }
}
```
Dagger容器中做了啥：

```kotlin
@Component(modules = [NetWorkModules::class,HomeModule::class])
interface ApplicationComponent {
    
    fun inject(activity:MainActivity)
    
}
```

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

```kotlin

@Module
class HomeModule {
    @Provides
    fun providerHomePresenter(api: ApiService): HomePresenter {
        return HomePresenterImpl(api)
    }

}
```

可见Dagger容器管理着字段的注入。然后管理了两个Module。

最后就是HomePresenterImpl实现类了

```kotlin

interface HomePresenter {
    fun logOut()
}

class HomePresenterImpl constructor(private val userService: ApiService) :HomePresenter {
    override fun logOut() {
           userService.logOutFromSever().enqueue(object : Callback<ResponseBody> {
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   Log.i("HomePresenterImpl", "onResponse")
               }

               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   Log.i("HomePresenterImpl", "onFailure")
               }
           })
    }
}

```

如上HomePresenterImpl构造添加了一个ApiService后，我们额外在NetWorkModules中定义了一个方法来提供ApiService的实例。要使用@Binds
该如何改写呢？如下：

```kotlin
@Module
abstract class HomeModule {
//    二者共存时：错误: A @Module may not contain both non-static and abstract binding methods
//    @Provides
//    fun providerHomePresenter(api: ApiService): HomePresenter {
//        return HomePresenterImpl(api)
//    }
    @Binds
    abstract fun bindHomePresenter(homePresenterImp: HomePresenterImpl): HomePresenter
}
```

- 抽象方法不需要我们自己实现
- 返回值类型定义为需要的类型
- 参数为类型的实现类


```kotlin
class HomePresenterImpl @Inject constructor(private val userService: ApiService) :HomePresenter {
    override fun logOut() {
           userService.logOutFromSever().enqueue(object : Callback<ResponseBody> {
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   Log.i("HomePresenterImpl", "onResponse")
               }

               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   Log.i("HomePresenterImpl", "onFailure")
               }
           })
    }
}
```
目标类上加注解，表示受Dagger容器管理。

接下来一一张形象的图来描绘下二者的对比：

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/binds.png)

可见@Binds和@providers是有区别的：

@Providers 需要自己提供方法，自己提供实例。

@Binds     需要定义方法，然后方法参数传递个实现类，实现类需要使用@Inject注解。













