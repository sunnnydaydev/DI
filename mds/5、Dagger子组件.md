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

###### 2、子组件&组件生命周期




