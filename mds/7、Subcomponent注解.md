# Subcomponent

子组件的出现就是方便单独管理某些对象的生命周期。除了将对象作用域限定为不同的生命周期之外，创建子组件是分别封装应用的不同部分的良好实践。

###### 1、首先看下用法

```kotlin
/**
 * Create by SunnyDay /12/19 21:31:51
 */
class UserInfo @Inject constructor()
```
```kotlin
@Subcomponent
interface LoginComponent{

    fun getUserInfo():UserInfo
    
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```

使用@Subcomponent注解定义个子组件，定义子组件时还需要定义子组件的创建方式。这样父组件知道如何创建子组件:

```kotlin
/**
 * 子容器被Module所管理，这里创建一个LoginModule，通过subcomponents制定所管理的子容器
 * */
@Module(subcomponents = [LoginComponent::class])
class LoginModule
```

定义个Module，其注解参数来引入子组件。

```kotlin
@Component(modules = [NetModule::class,PersonModule::class,LoginModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)

    fun getLoginComponent():LoginComponent.Factory
}
```
父容器管理含有子组件的Module，这样父组件就知道如何创建子组件对象了。

```kotlin
class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { 
        (application as MyApplication).getContainer()
    }
    
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("my-test","userInfo login activity:${loginComponent.getUserInfo()}")
    }
}
```
使用也很简单直接父组件中获取到子组件对象，这样就可使用子组件操作其管理的对象了。


这里我们需要明白：

（1）@Inject注解标记的对象可被任何容器管理。

如上我们在LoginComponent中获取了其对象，其实AppComponent也可以管理使用它，不妨我们试下：

```kotlin
class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }

    @Inject
    lateinit var userInfo: UserInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("my-test", "userInfo main activity:${userInfo}")
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
```

发现log成功打印了~

（2）子组件可使用父组件管理的对象

看下父组件管理的Module

```kotlin
@Component(modules = [NetModule::class,PersonModule::class,LoginModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)

    fun getLoginComponent():LoginComponent.Factory
}
```

```kotlin
@Module
class NetModule {

    @Provides
    fun providerApiService(client:OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(client)
            .build()
            .create(ApiService::class.java)

    @Provides
    fun providerOkHttpClient():OkHttpClient = OkHttpClient.Builder().build()
}
```

如上，父组件管理的ApiService我们在子组件中可正常使用

```kotlin
@Subcomponent
interface LoginComponent{

    // 获取父组件管理的对象
    fun getApiService():ApiService
    
    fun getUserInfo():UserInfo
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```

```kotlin
class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("my-test","userInfo login activity:${loginComponent.getUserInfo()}")
        // 使用父组件管理的对象
        Log.d("my-test","api service login activity:${loginComponent.getApiService()}")
    }
}
```

既然子组件的出现是方便管理对象的生命周期的，那么我们看下不同容器生命周期的处理。

###### 2、生命周期管理

我们知道使用父容器时一般在application的onCreate种提供容器的实例，这样这个容器就是整个应用唯一的实例。 子组件也类似，在使用子组件时则一般在Activity中提供子组件实例的获取，这样这个子容器activity生命周期内唯一。

Dagger的作用域有如下规定:

(1)如果某个类标记有作用域注解，该类就只能由带有相同作用域注解的容器管理。

啥意思呢？就是UserRepository对象通过@Singleton注解限定作用域，ApplicationComponent容器管理了UserRepository对象。假如此时有个TestComponent想要直接管理UserRepository对象：

```kotlin
@Component
interface TestComponent {
    fun  getUserRepository(): UserRepository
}
```
直接报错 TestComponent (unscoped) may not reference scoped bindings，正确的做法是给TestComponent也加上@Singleton注解。

(2)如果某个组件标记有作用域注解，该组件就只能提供带有该注解的类型或不带注解的类型。

这个很直白，假如ApplicationComponent带有@Singleton标记，则ApplicationComponent可管理的对象有两种：

- 管理的对象带有@Singleton标记
- 管理的对象不含@Singleton标记

(3)子组件不能使用其某一父组件使用的作用域注解

也就是父容器ApplicationComponent使用@Singleton标记后，这个标记就不能被子容器SubApplicationComponent使用了，SubApplicationComponent可自定义个作用域范围使用。
