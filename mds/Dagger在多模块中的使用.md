# Dagger 在多模块的实践

假如我们当前有一个app 工程和一个 login module，使用Dagger容器进行全局统一管理。我们可能这样做：

###### 1、app module

```kotlin
@Component
interface AppContainer{
    fun injectMainActivity(mainActivity: MainActivity)
}
```

```kotlin
/**
 * Create by SunnyDay /12/10 13:56:09
 */
class MyApplication : Application() {

    private val container: AppContainer = DaggerAppContainer.create()
    override fun onCreate() {
        super.onCreate()
    }

    fun getAppComponent () = container
}
```

```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).getAppComponent().injectMainActivity(this)
        super.onCreate(savedInstanceState)
        startActivity(Intent(this,LoginActivity::class.java))
    }

}
```

很简单，打开app时直接加载login module的LoginActivity~

###### 2、Login module

```kotlin
/**
 * Create by SunnyDay /12/10 14:19:32
 */
class User @Inject constructor()
```

```kotlin
/**
 * Create by SunnyDay /12/10 14:21:03
 */

@Component
interface LoginContainer {
    fun getUser():User

    fun  injectLoginActivity(loginActivity: LoginActivity)
}
```

###### 3、Dagger 容器统一管理

试想一下，假如我们不考虑使用Dagger容器统一管理时 我们可能就这样去做了：

```kotlin
class LoginActivity : AppCompatActivity() {

  private  val loginContainer :LoginContainer = DaggerLoginContainer.create()

    @Inject
    lateinit var user: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        loginContainer.injectLoginActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<AppCompatTextView>(R.id.tvText).text = user.javaClass.simpleName

    }
}
```

没毛病，程序能够正常跑起来，然后这样就不能方便的进行统一管理了，因此我们可能会这样去做：

```kotlin
@Component
interface AppContainer{
    fun injectMainActivity(mainActivity: MainActivity)
    
    fun getLoginComponent():LoginContainer
}
```

```kotlin
class LoginActivity : AppCompatActivity() {
    
  // LoginActivity 在 Login module 这个module 在 app Module的下层，下层都无法使用上层的依赖，MyApplication这里无法使用
  private  val loginContainer :LoginContainer = (application as MyApplication).getAppComponent().getLoginComponent().create()

    @Inject
    lateinit var user: User
    
    override fun onCreate(savedInstanceState: Bundle?) {
        loginContainer.injectLoginActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<AppCompatTextView>(R.id.tvText).text = user.javaClass.simpleName

    }
}
```

哈哈哈😁，写的时候AppContainer这里心里就可能嘀咕，这样可行吗？ 看来果然行不通，我们需要改正->

（1）功能模块提供接口

```kotlin
/**
 * Create by SunnyDay /12/10 14:46:09
 */
interface ProviderLoginComponent {
    fun providerLoginComponent():LoginContainer
}
```

（2）app 模块的Application实现这个接口

```kotlin
@Component(modules = [ProviderModule::class])
interface AppContainer{
    fun injectMainActivity(mainActivity: MainActivity)

    // 1、去除modules = [ProviderModule::class]
    // 2、fun getLoginContainer(): LoginContainer = DaggerLoginContainer.create()
    // 这种做法行不通
    fun getLoginContainer(): LoginContainer
}
```

```kotlin
class MyApplication : Application(),ProviderLoginContainer {

    private val container: AppContainer = DaggerAppContainer.create()
    override fun onCreate() {
        super.onCreate()
    }

    fun getAppComponent () = container
    override fun providerLoginContainer(): LoginContainer = container.getLoginContainer()
}
```

（3）功能模块使用

```kotlin
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        val  loginContainer :LoginContainer = (application as ProviderLoginContainer).providerLoginContainer()
        loginContainer.injectLoginActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<AppCompatTextView>(R.id.tvText).text = user.javaClass.simpleName

    }
}
```

疑问

- 为啥上面这种方式不行？

```kotlin
// 1、去除modules = [ProviderModule::class]
// 2、fun getLoginContainer(): LoginContainer = DaggerLoginContainer.create()
// 这种做法行不通
```


- 官方文档这里的具体实现细节？

```kotlin
loginComponent = (applicationContext as MyDaggerApplication)
                        .appComponent.loginComponent().create()
```

使用了Build模式？ 如下方式我未跑起来

```kotlin
@Component
interface LoginContainer {
    fun getUser():User

    fun  injectLoginActivity(loginActivity: LoginActivity)

    @Component.Builder
    interface Builder {
        fun build(): LoginContainer
    }

}
```

总结：

感觉上述方式通过接口解决了模块之间无法引用问题，但不如直接在Login module创建LoginContainer看着简单

