# Dagger Module

# @Provider

这两个注解的作用类似，但是写法上有区别。

###### 引申


现在有如下场景，MVP的方式模拟一个用户登录，若继续使用@Inject方式则是这样的：

定义一个retrofit接口 ->

```kotlin
interface LoginRetrofitService {
    @GET("/")
    fun login(): Call<ResponseBody>
}
```
定义presenter持有依赖这个接口 ->

```kotlin
class LoginPresent @Inject constructor(val loginRetrofitService: LoginRetrofitService)
```

定义Dagger容器提供present对象 ->

```kotlin
@Component
interface ApplicationComponent {
     fun  getLoginPresent():LoginPresent
}
```

恩 ？ 好像少了点什么， LoginPresent依赖的对象也要加上@Inject注解的，但是这里LoginRetrofitService是一个接口，能加码？构造都没有，根本加不上，，，

此时@Provider与@Binds就排上用场了。

###### 基本用法

直接创建一个Module类 ->

```kotlin
/**
 * Create by SunnyDay /12/08 21:31:47
 */
@Module
class ProviderModule {
    /**
     * LoginRetrofitService对象的提供。
     *
     * 当Dagger创建某个类的对象时，发现此类没有被@Inject注解构造，则就去自己管理的Module中去找@Provides注解的方法。
     * 当方法的返回值符合时直接使用。
     * */
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://a.bcom/login/")
            .build()
            .create(LoginRetrofitService::class.java)
    }
}
```

修改容器管理方式，让容器支持对目标模块的管理 ->

```kotlin
@Component(modules = [ProviderModule::class])
interface ApplicationComponent {
     fun  getLoginPresent():LoginPresent
}
```

测试用法 ->

```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val present= daggerContainer.getLoginPresent()
        println("loginService:${present.loginRetrofitService}")
        //I/System.out: loginService:retrofit2.Retrofit$1@f22b244
    }
}
```

###### 变形：provideLoginRetrofitService方法需要参数？

假如有这个需求，如provideLoginRetrofitService需要传递一个OkHttpClient对象->

```kotlin
@Provides
fun provideLoginRetrofitService(client: OkHttpClient): LoginRetrofitService {
    return Retrofit.Builder()
        .baseUrl("https://a.bcom/login/")
        .client(client)
        .build()
        .create(LoginRetrofitService::class.java)
}
```
这个OkHttpClient的对象该如何传递过来呢？ 可以这样做,在本Module中再定义个方法即可 ->

```kotlin
@Module
class ProviderModule {
    /**
     * LoginRetrofitService对象的提供。
     *
     * 当Dagger创建某个类的对象时，发现此类没有被@Inject注解构造，则就去自己管理的Module中去找@Provides注解的方法。
     * 当方法的返回值符合时直接使用。
     * */
    @Provides
    fun provideLoginRetrofitService(client: OkHttpClient): LoginRetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://a.bcom/login/")
            .client(client)
            .build()
            .create(LoginRetrofitService::class.java)
    }

    @Provides
    fun providerOkhttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}
```

###### 变形：子Module可以继承父Module中方法

上面的例子，把providerOkhttpClient方法抽取到基类 ->

```kotlin
/**
 * Create by SunnyDay /12/08 21:49:18
 */
@Module
open class BaseProviderModule {
    @Provides
    fun providerOkhttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}
```
这样还是能跑起来的但是要注意两点：
- 父Module BaseProviderModule的@Module注解不能少
- 容器ApplicationComponent中不用注册BaseProviderModule

###### 变形:Module也可结合@Inject使用

还是上面的例子，假如provideLoginRetrofitService 需要一个普通对象UserBean ->

```kotlin
class UserBean @Inject constructor()
```

```kotlin
@Module
class ProviderModule : BaseProviderModule() {
    /**
     * LoginRetrofitService对象的提供。
     *
     * 当Dagger创建某个类的对象时，发现此类没有被@Inject注解构造，则就去自己管理的Module中去找@Provides注解的方法。
     * 当方法的返回值符合时直接使用。
     * */
//    @Provides
//    fun provideLoginRetrofitService(client: OkHttpClient): LoginRetrofitService {
//        return Retrofit.Builder()
//            .baseUrl("https://a.bcom/login/")
//            .client(client)
//            .build()
//            .create(LoginRetrofitService::class.java)
//    }
// Cannot have more than one binding method with the same name in a single module

    @Provides
    fun provideLoginRetrofitService(userBean: UserBean): LoginRetrofitService {
        // todo use userBean data here
        return Retrofit.Builder()
            .baseUrl("https://a.bcom/login/")
            .build()
            .create(LoginRetrofitService::class.java)
    }

}
```
Dagger在执行到provideLoginRetrofitService方法时发现需要参数，就会在其管理的@Inject和Module中进行查找，然后使用。

注意上述我把带OkHttpClient参数的注释了，因为只能有一个方法提供LoginRetrofitService实例。

###### 当Module中也存在时以哪个为主？

```kotlin
@Module
class ProviderModule : BaseProviderModule() {
    /**
     * LoginRetrofitService对象的提供。
     *
     * 当Dagger创建某个类的对象时，发现此类没有被@Inject注解构造，则就去自己管理的Module中去找@Provides注解的方法。
     * 当方法的返回值符合时直接使用。
     * */
//    @Provides
//    fun provideLoginRetrofitService(client: OkHttpClient): LoginRetrofitService {
//        return Retrofit.Builder()
//            .baseUrl("https://a.bcom/login/")
//            .client(client)
//            .build()
//            .create(LoginRetrofitService::class.java)
//    }
// Cannot have more than one binding method with the same name in a single module

    @Provides
    fun provideLoginRetrofitService(userBean: UserBean): LoginRetrofitService {
        // todo use userBean data here
        return Retrofit.Builder()
            .baseUrl("https://a.bcom/login/")
            .build()
            .create(LoginRetrofitService::class.java)
    }

    @Provides
    fun provideUserBean():UserBean{
        println("provideUserBean-in module")
        return UserBean()
    }
}
```

测试了下发现log打印出来了，应该是就近原则以module提供为主：I/System.out: provideUserBean-in module

###### 变形：支持跨模块使用？

稍作修改，新建一个TestModule，把providerOkhttpClient从BaseProviderModule移动到TestModule

```kotlin
@Module
class TestModule {
    @Provides
    fun providerOkhttpClient(): OkHttpClient{
        println("providerOkhttpClient")
       return OkHttpClient.Builder().build()
    }
}
```
这样完全是ok的，Dagger回自己查找。

# @Binds注解用法

###### 1、栗子

MVP栗子，我们稍作修改 ->

```kotlin
interface ILoginPresent {
    fun login()
}
class LoginPresent @Inject constructor(private val loginRetrofitService: LoginRetrofitService):ILoginPresent {
    override fun login() {
        loginRetrofitService.login()
    }
}
```
```kotlin
@Component(modules = [ProviderModule::class])
interface ApplicationComponent {
     fun  getLoginPresent():LoginPresent
}
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)
        
        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val present= daggerContainer.getLoginPresent()
        present.login()
    }
}
```

仔细观察，没啥不同，就是加了个接口，然后实现了下它。对Dagger毫无影响。那么我们稍作改变 ->

```kotlin
@Component(modules = [ProviderModule::class])
interface ApplicationComponent {
     fun  getLoginPresent():ILoginPresent //就这一点，返回值改为接口
}
```

运行直接crash：ILoginPresent cannot be provided without an @Provides-annotated method.

此时就是@Binds上场的时候了,为了不混淆，我们单独再搞个BindsModule

```kotlin
@Module
abstract class BindsModule {
   @Binds
   abstract fun provideLoginPresent(loginPresent: LoginPresent): ILoginPresent
}

```
- 注意这里module定义为抽象的
- 方法定义为抽象的，方法返回值为接口，方法添加参数，参数为具体的实现类。
- 参数可为@Inject提供，@Provider提供自己实现。

容器注册下，让这个Module也受容器管理~

```kotlin
@Component(modules = [ProviderModule::class,BindsModule::class])
interface ApplicationComponent {
     fun  getLoginPresent():ILoginPresent
}
```

ojbk 程序可以跑了。 梳理下流程 ->

```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val present= daggerContainer.getLoginPresent()
        present.login()
        //首先这里触发present的获取，由于getLoginPresent()返回的是接口类型。Dagger就去管理的模块中寻找返回值是ILoginPresent类型的方法
    }
}
```

###### 注意

如下是不允许的

```kotlin
/**
 * Create by SunnyDay /12/08 22:33:51
 */
@Module
abstract class BindsModule {
   @Binds
   abstract fun provideLoginPresent(loginPresent: LoginPresent): ILoginPresent

   @Provides
   fun providerOkhttpClient(): OkHttpClient = OkHttpClient.Builder().build()
}
```

A @Module may not contain both non-static and abstract binding methods

# 总结

@Inject，@Provider，@Binds可以混合使用，Dagger容器管理着一张庞大的关系图，只要由这三者之一提供创建对象的方式即可。


