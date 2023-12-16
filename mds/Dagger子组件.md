# Dagger子组件

可以理解为父组件为父类，子组件就是子类。因此子组件可以使用父组件中的对象，除此之外我感觉子组件的另外一个好处就是方便单独管理某些对象的生命周期。

总结到这里可以简单使用一张图来概括下目前对Dagger的认识：

![NewGet](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/newget.png)

一般来说Module之间的对象可以相互调用，对象1可以使用Module的对象。子组件可以使用父组件的对象。

首先看一个关系图梳理：

![1](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/1.png)

如上图，是结合[登录栗子手动依赖注入](2、登录栗子手动依赖项注入.md)来使用Dagger自动依赖注入进行了改写。在改写过程中可能会涉及到如下：

- 子组件的使用：如何使用子组件
- 子组件的生命周期：组件生命周期在子组件上的应用、注意点。

本章节就把这些一块综合下。

###### 1、子组件定义及其使用

首先看看子组件如何定义的：其实很简单，使用@Subcomponent注解标注下即可。注意定义子组件同时需要定义子组件对象的创建方式，这样Dagger
父容器才知道在哪里、如何获取子组件对象。

```kotlin
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)

    // 提供创建子容器对象的接口，这样父容器知道如何创建子容器对象。
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```

这样子组件就定义好了，定义好之后需要通过Module方式引入。注意这里使用的注解参数subcomponents代表引入的是子组件。多个子组件使用逗号隔开即可。

这里需要留意回顾下组件引入module的方式是在@Component中使用modules=【xxx】的方式。

```kotlin
/**
 * Create by SunnyDay /07/16 18:05:08
 */
@Module(subcomponents = [LoginComponent::class] )
class SubcomponentsModule 
```

好了吧子组件定义好后就是让父组件来进行管理了，很简单，父组件管理子组件时也是采取module的方式管理：

```kotlin
// 向父组件注册子Module
@Singleton
@Component(modules = [NetWorkModules::class,HomeModule::class,SubcomponentsModule::class])
interface ApplicationComponent {

    fun getUserRepository():UserRepository

    fun inject(activity:MainActivity)
    // 提供子组件对象
    fun getLoginComponent():LoginComponent.Factory
}
```

一顿操作猛如虎，接下来看看怎样使用的：

```kotlin
class LoginActivity : AppCompatActivity() {
    companion object {
        const val tag = "MainActivity"
    }
    // 4、此时这里就会被赋值了
    @Inject
    lateinit var loginPresent: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //1、获取Application
        val application = application as MyApplication
        //2、获取子组件对象
        val loginComponent = application.appComponent.getLoginComponent().create()
        //3、字段注入
        loginComponent.inject(this)
        setContentView(R.layout.activity_injecct_field)
         //5、使用
        loginPresent.login()
    }
}
```

###### 2、组件生命周期再次理解

容器管理着对象依赖关系图，若是不希望每次创建某对象，可以把对象的生命周期限定为容器的生命周期即可。具体做法也很简单主要分为两步，
首先给容器添加@Singleton注解（也可以使用自定义注解），其次就是目标类也进行标记即可。这点我们已经在Dagger基础中有所了解。

其实我们可以使用任何自定义注解在容器中指定某个类的唯一实例，只要该容器和类带有该注解即可。@Singleton 属于 Dagger 库默认提供的。


这里先对目前我们了解的组件生命周期使用做下总结：

- 使用构造函数注入（通过 @Inject）时，应在类中添加作用域注解
- 使用 Dagger 模块时，应在 @Provides 方法中添加作用域注解


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

```kotlin
@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {
    fun inject(activity: LoginActivity)
}

@Singleton
class UserRepository @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource // 类构造中持有LoginRetrofitService实例。
) 

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService 
}
```

###### 3、子组件生命周期

有了上面的子组件改写后，我们来进行下简单的分析：

LoginComponent是在 Activity 的 onCreate() 方法中创建的，将随着 Activity 的销毁而被隐式销毁。

每次请求时，LoginComponent 必须始终提供 LoginVPresenter 的同一实例。您可以通过创建自定义注释作用域并使用该作用域为 LoginComponent添加注解确保这一点。
请注意，不可使用 @Singleton 注释，因为该注释已被父组件使用，您需要创建不同的注释作用域。

为啥不能使用@Singleton 注释呢？Dagger对作用于有限定规则的：

- 如果某个类型标记有作用域注解，该类型就只能由带有相同作用域注解的组件使用。
- 如果某个组件标记有作用域注解，该组件就只能提供带有该注解的类型或不带注解的类型。
- 子组件不能使用其某一父组件使用的作用域注解。

因此我们需要为子组件定义新的作用域注解：

```kotlin
/**
 * Create by SunnyDay /07/17 21:09:29
 */
@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope
```

子组件中的某些对象限定为单例直接按照父组件的用法即可：

```kotlin
@ActivityScope
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)

    // 提供创建子容器对象的接口，这样父容器知道如何创建子容器对象。
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```
```kotlin
@ActivityScope
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

可见很简单HomePresenterImpl受Subcomponent管理，而且是构造型因此直接给类加个和容器相同的注解即可。

假如LoginActivity有两个Fragment都需要同一个loginPresenter实例，这时就可以这样做：

```kotlin
@ActivityScope
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)

    // 提供创建子容器对象的接口，这样父容器知道如何创建子容器对象。
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
    fun inject(usernameFragment: LoginUsernameFragment)
    fun inject(passwordFragment: LoginPasswordFragment)
}
```
然后对应的LoginUsernameFragment中定义个loginPresenter字段注入下即可。这样这两个fragment与我们的LoginActivity这三处使用的都是同一个
实例。




