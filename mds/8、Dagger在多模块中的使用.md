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

很简单实用接口做了解耦，而loginContainer对象的获取来自具体的接口实现类MyApplication#ProviderLoginContainer这里。ProviderLoginContainer具体是通过AppContainer中定义的接口获取的，此时
Dagger会找到ProviderModule来提供对象。


- 官方文档这里的具体实现细节？

```kotlin
loginComponent = (applicationContext as MyDaggerApplication)
                        .appComponent.loginComponent().create()
```

这里有一个小细节[官方文档](https://developer.android.google.cn/training/dependency-injection/dagger-multi-module?hl=zh-cn)中使用Dagger子容器方式实现的。我心里突发奇想使用Dagger容器实现了下~
具体大家可看下官方实现。

总结：

感觉多模块就是在两容器之间添加了接口来解决多模块之间不能直接交流的情况~ 这里也加深了对接口的了解。

