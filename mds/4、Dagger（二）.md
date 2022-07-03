# 登录流程的Dagger实现

有了Dagger基础后这里使用Dagger来实现登录流程的自动依赖注入，顺便再熟悉下Dagger基础中的注解，顺带再熟悉下新的注解，案例驱动搞起来~

###### 1、首先构建依赖关系图

```kotlin
/**
 * Create by SunnyDay /07/03 20:23:40
 */
data class UserRepository @Inject constructor(val userRemoteDataSource:UserRemoteDataSource)
```
```kotlin
/**
 * Create by SunnyDay /07/03 20:24:55
 */
data class UserRemoteDataSource @Inject constructor(val loginService: LoginRetrofitService) {}
```
这样UserRepository依赖UserRemoteDataSource对象，UserRemoteDataSource依赖LoginRetrofitService的关系图则创建好了。
这样Dagger便会自动构建好依赖关系，创建实例对象。

###### 2、特殊对象实例的提供

其实上述中LoginRetrofitService的代码未给出，因为这个属于特殊对象的创建，我们不能使用@Inject构造方式创建。还好Dagger提供了另外一种对象创建方案

```kotlin
/**
 * Create by SunnyDay /07/03 20:26:17
 *
 * LoginRetrofitService 为Retrofit的service实例，这里不能使用@Inject构造的方案来创建实例。应该使用模块+@Provides方式。
 */
interface LoginRetrofitService {
    @GET("/")
    fun getLoginDataFromSever():Call<ResponseBody>
}
```
代码就是普通的代码，并且未使用@Inject构造。这里我们可以使用如下方案提供LoginRetrofitService实例：

```kotlin
/**
 * Create by SunnyDay /07/03 20:39:57
 * 这里使用@Module+@Provides注解后，当依赖关系图中需要LoginRetrofitService实例时可直接自动从这里取。
 * 参见[com.example.auto_di.repository.UserRemoteDataSource],这个类构造需要LoginRetrofitService，但是LoginRetrofitService
 * 未使用@Inject注解自己的构造。
 */
@Module
class NetWorkModule {
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .build()
            .create(LoginRetrofitService::class.java)
    }
}
```

###### 3、创建容器
```kotlin
/**
 * Create by SunnyDay /07/03 20:10:47
 */
@Component(modules = [NetWorkModule::class])
interface ApplicationComponent {
    /**
     * 为LoginActivity注入字段。
     * */
    fun inject(activity: LoginActivity)
}
```
很简单，使用带参数的@Component直径对应Module即可。这样容器管理依赖关系图时就知道如何获取特殊对象了。

inject方法为我们定义的一个新方法，这个方法是为了字段的注入而定义的。因为activity属于特殊类，我们不能为他直接new 实例，所以为其定义的字段我们只能
使用set注入的方案。

如需将类型添加到 Dagger 图，建议的方法是使用构造函数注入（即在类的构造函数上使用 @Inject 注释）。有时，此方法不可行，您必须使用 Dagger 模块。例如，您希望 Dagger 使用计算结果确定如何创建对象实例时。每当必须提供该类型的实例时，Dagger 就会运行 @Provides 方法中的代码。

示例中的 Dagger 图目前如下所示：

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/AutoDI.png)

图的入口点为 LoginActivity。由于 LoginActivity 注入了 LoginViewModel，因此 Dagger 构建的图知道如何提供 LoginViewModel 的实例，以及如何以递归方式提供其依赖项的实例。Dagger 知道如何执行此操作，因为类的构造函数上有 @Inject 注释。

在由 Dagger 生成的 ApplicationComponent 内，有一种 factory 类型方法，可用于获取它知道如何提供的所有类的实例。在本例中，Dagger 委托给 ApplicationComponent 中包含的 NetworkModule 来获取 LoginRetrofitService 的实例。

###### 4、使用

```kotlin
class LoginActivity : AppCompatActivity() {
    //标记字段
    @Inject
    lateinit var loginViewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 初始化注入方法，原理很简单，把activity实例给Dagger，dagger则可以动态为activity对象成员赋值。
        (application as MyApplication).component.inject(this@LoginActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel.login()
    }
}
```








