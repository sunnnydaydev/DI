# Subcomponent

子组件的出现就是方便单独管理某些对象的生命周期。除了将对象作用域限定为不同的生命周期之外，创建子组件是分别封装应用的不同部分的良好实践。

首先看下用法

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

# 子组件的生命周期管理

在[Dagger基础.md](./3、Dagger基础.md)这里我们了解到了容器单例的使用，并且使用Dagger默认的@Singleton实现了单例。

那么子组件的生命周期的场景是怎样的呢？以常见的登录流程为🌰，假如登录页面是LoginActivity，对应的处理逻辑都在LoginViewModel中。
我们期望在每次Login流程中LoginViewModel的实例是单例的。此时我们再使用@Singleton标记LoginViewModel就不符合要求了，这样会导致
整个app生命周期内对象单例因此我们可以定义个自定scope来处理这个。


下面我们结合前面子组件的栗子来讲解下

首先定义个LoginViewModel

```kotlin
class LoginViewModel @Inject constructor()
```
接下来定义子组件并提供注入LoginActivity的api


```kotlin
@Subcomponent
interface LoginComponent {
    
    fun inject(activity: LoginActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}
```
建立Module 指定对应子组件
```kotlin
@Module(subcomponents = [LoginComponent::class])
class LoginModule
```

LoginModule注册到AppComponent中，让AppComponent管理

```kotlin
@Component(modules = [NetModule::class,PersonModule::class,LoginModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)

    fun getLoginComponent():LoginComponent.Factory
}
```

看下效果

```kotlin
class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    @Inject
    lateinit var loginView1:LoginViewModel

    @Inject
    lateinit var loginView2:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginComponent.inject(this)
        setContentView(R.layout.activity_login)
        
        Log.d("my-test","loginView1:${loginView1}")
        Log.d("my-test","loginView2:${loginView2}")
        //loginView1:com.example.daggerreview.entity.LoginViewModel@ab533c4
        //loginView2:com.example.daggerreview.entity.LoginViewModel@284f2ad
    }
}
```

很简单，我们只是在子容器的栗子上加了一点代码，为LoginActivity添加了一个成员字段LoginViewModel，通过Login可以发现此时获取到了两个实例，这在单个
activity中无可厚非，但可能有如下场景LoginActivity持有多个fragment我们假定为LoginFragmentA、LoginFragmentB，而LoginViewModel需要注入到
这两个fragment中，此时就出问题了，两个fragment会持有两份viewModel的实例，这明显不满足我们的需求，我们要求一次完整的登录流程中viewModel的实例是
一份的，否则会出现奇怪的问题。好了接下来我们就使用作用域来解决下：

首先定义一个ActivityScope的自定义Scope注解，很简单关键点就是使用Dagger2提供的@Scope标签标记我们自定义的注解。
```kotlin
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope
```

接下来就是使用了

```kotlin
@ActivityScope
class LoginViewModel @Inject constructor()
```
```kotlin
@ActivityScope
@Subcomponent
interface LoginComponent {
    fun inject(activity: LoginActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}
```

```kotlin
class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    @Inject
    lateinit var loginView1:LoginViewModel

    @Inject
    lateinit var loginView2:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginComponent.inject(this)
        setContentView(R.layout.activity_login)

        Log.d("my-test","loginView1:${loginView1}")
        Log.d("my-test","loginView2:${loginView2}")

        //loginView1:com.example.daggerreview.entity.LoginViewModel@a45233a
        //loginView2:com.example.daggerreview.entity.LoginViewModel@a45233a
    }
}
```

很简单为容器，以及对象贴上标签即可，但这里需要注意假如不给容器贴上这个标签会编译报错：

(unscoped) may not reference scoped bindings: It is also requested at:LoginComponent

报错很明显，让我们为对应的容器打上标签。


###### 注意点

(1)关于作用域的命名

我们可能已将此作用域命名为 @LoginScope，但这种做法并不理想。作用域注释的名称不应明确指明其实现目的。相反，作用域注释应根据其生命周期进行命名，因为注释可以由同级组件（如 RegistrationComponent 和 SettingsComponent）重复使用。因此，您应将其命名为 @ActivityScope 而不是 @LoginScope

(2)如果某个类标记有作用域注解，该类就只能由带有相同作用域注解的容器管理。

啥意思呢？就是UserRepository对象通过@Singleton注解限定作用域，ApplicationComponent容器管理了UserRepository对象。假如此时有个TestComponent想要直接管理UserRepository对象：

```kotlin
@Component
interface TestComponent {
    fun  getUserRepository(): UserRepository
}
```
直接报错 TestComponent (unscoped) may not reference scoped bindings，正确的做法是给TestComponent也加上@Singleton注解。

(3)假如TestComponent是ApplicationComponent的子组件，TestComponent 不用标记@Singleton，可以管理@Singleton标记的对象。

(2)如果某个组件标记有作用域注解，该组件就只能提供带有该注解的类型或不带注解的类型。

这个很直白，假如ApplicationComponent带有@Singleton标记，则ApplicationComponent可管理的对象有两种：

- 管理的对象带有@Singleton标记
- 管理的对象不含@Singleton标记

(3)子组件不能使用其某一父组件使用的作用域注解

也就是父容器ApplicationComponent使用@Singleton标记后，这个标记就不能再标记在子容器LoginComponent上了，LoginComponent可自定义个作用域范围使用。
