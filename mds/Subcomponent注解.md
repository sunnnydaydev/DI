# Subcomponent

可以理解为父容器为父类，子容器就是子类。因此子容器可以使用父容器中的对象，除此之外我感觉子组件的另外一个好处就是方便单独管理某些对象的生命周期。

###### 1、首先看下用法

```kotlin
/**
 * Create by SunnyDay /12/12 17:34:59
 */
@Subcomponent
interface SubApplicationComponent {

    fun  getUserRepository(): UserRepository

    /**
     * 创建工厂类，提供创建子组件的方法。
     * */
    @Subcomponent.Factory
    interface Factory{
        fun create():SubApplicationComponent
    }
}
```

```kotlin
/**
 * Create by SunnyDay /12/12 17:50:59
 */
@Module(subcomponents = [SubApplicationComponent::class])
class SubApplicationModule
```


```kotlin
/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component(modules = [SubApplicationModule::class])
interface ApplicationComponent {
    fun getSubApplicationComponent(): SubApplicationComponent.Factory
}
```


```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)
        //1、获取父容器
        val daggerContainer = (application as MyApplication).getDaggerContainer()
        // 获取父容器管理的子容器
        val subApplicationComponent = daggerContainer.getSubApplicationComponent().create()
        // 子容器中取对象
        val repo = subApplicationComponent.getUserRepository()

    }
}
```