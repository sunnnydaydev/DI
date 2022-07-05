# Dagger子组件

单例的生命周期过长，通常是Application级别的，这时可以使用Dagger子组件来管理一些数据，让这些数据控制在一定的组件生命周期内。

###### 1、子组件的定义

```kotlin
@Subcomponent
interface LoginComponent {
    // 注入操作放在子组件中进行
    fun inject(activity:LoginActivity)
    //必须：再定义个子组件Factory，便于ApplicationComponent知道如何创建LoginComponent实例
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```

@Subcomponent 表明这是一个子组件

@Subcomponent.Factory 定义子组件的实例工厂获取类，工厂也必须提供一个方法，用于创建子组件实例。

###### 2、告知父组件我是你的子组件

这个需要以module方式声明

```kotlin
@Module(subcomponents = [LoginComponent::class])
class SubcomponentsModule {}
```

并且把模块注册到父组件即可。

```kotlin
@Singleton
@Component(modules = [NetWorkModule::class, SubcomponentsModule::class])
interface ApplicationComponent {
    fun loginComponent(): LoginComponent.Factory
}
```
这样在父组件便可以直接定义相应的api，以获取子组件实例了。


###### 3、如何使用？
```kotlin
class LoginActivity : AppCompatActivity() {
    // 生命周期跟随activity
    lateinit var loginComponent: LoginComponent
    @Inject
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        loginComponent = (application as MyApplication).component.loginComponent().create()
        loginComponent.inject(this@LoginActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel.login()
    }
}
```

可见loginComponent的生命周期是附加到activity的生命周期上的。而不是Application的。

需要注意这里的LoginComponent为单例的，因为容器组件使用单例标记了。

```kotlin
@Singleton
@Component(modules = [NetWorkModule::class, SubcomponentsModule::class])
interface ApplicationComponent {
    fun loginComponent(): LoginComponent.Factory
}
```

```kotlin
class LoginActivity : AppCompatActivity() {
    // 生命周期跟随activity
    lateinit var loginComponent: LoginComponent
    @Inject
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        loginComponent = (application as MyApplication).component.loginComponent().create()
        loginComponent.inject(this@LoginActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel.login()

        val loginComponent2 = (application as MyApplication).component.loginComponent().create()
        Log.i("tag","loginComponent：$loginComponent")
        Log.i("tag","loginComponent2：$loginComponent2")
        // 打印结果一致
    }
}
```


###### 4、为子组件分配作用域

由于父组件ApplicationComponent已经使用过@Singleton所以子组件变不能使用。具体规则如下：

- 如果某个类型标记有作用域注释，该类型就只能由带有相同作用域注释的组件使用。
- 如果某个组件标记有作用域注释，该组件就只能提供带有该注释的类型或不带注释的类型。
- 子组件不能使用其某一父组件使用的作用域注释。

（1）自定义作用域

```kotlin
@Scope
@Retention(value = AnnotationRetention.RUNTIME)
annotation class ActivityScope
```

（2）使用
```kotlin
@ActivityScope
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)
    fun inject(usernameFragment: LoginUsernameFragment)
    fun inject(passwordFragment: LoginPasswordFragment)

    //必须：再定义个子组件Factory，便于ApplicationComponent知道如何创建LoginComponent实例
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
```

```kotlin
@ActivityScope
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) {
    ...
}
```

下面两个Fragment中loginViewModel为同一个实例。

```kotlin
class LoginUsernameFragment:Fragment() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as LoginActivity).loginComponent.inject(this)

    }
}
```

```kotlin
class LoginPasswordFragment:Fragment() {
    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as LoginActivity).loginComponent.inject(this)
    }
}
```

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/subCom.png)

- NetworkModule（以及由此产生的 LoginRetrofitService）包含在 ApplicationComponent 中，因为您在组件中指定了它。

- UserRepository 保留在 ApplicationComponent 中，因为其作用域限定为 ApplicationComponent。如果项目扩大，您会希望跨不同功能（例如注册）共享同一实例。 由于 UserRepository 是 ApplicationComponent 的一部分，其依赖项（即 UserLocalDataSource 和 UserRemoteDataSource）也必须位于此组件中，以便能够提供 UserRepository 的实例。

- LoginViewModel 包含在 LoginComponent 中，因为只有 LoginComponent 注入的类才需要它。LoginViewModel 未包含在 ApplicationComponent 中，因为 ApplicationComponent 中的任何依赖项都不需要 LoginViewModel。

同样，如果您尚未将 UserRepository 的作用域限定为 ApplicationComponent，Dagger 会自动将 UserRepository 及其依赖项作为 LoginComponent 的一部分包含在内，因为这是目前使用 UserRepository 的唯一位置。 

除了将对象作用域限定为不同的生命周期之外，创建子组件是分别封装应用的不同部分的良好做法。 

根据应用流程构建应用以创建不同的 Dagger 子图有助于在内存和启动时间方面实现性能和扩容性更强的应用。




















