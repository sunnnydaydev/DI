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



















