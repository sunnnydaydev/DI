# Subcomponent

子组件的出现就是方便单独管理某些对象的生命周期。

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

使用@Subcomponent注解定义个子组件，定义子组件时还需要定义子组件的创建方式。这样父组件知道如何创建子组件。

```kotlin
/**
 * Create by SunnyDay /12/12 17:50:59
 */
@Module(subcomponents = [SubApplicationComponent::class])
class SubApplicationModule
```

定义个Module，其注解参数来引入子组件。

```kotlin
/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component(modules = [SubApplicationModule::class])
interface ApplicationComponent {
    fun getSubApplicationComponent(): SubApplicationComponent.Factory
}
```
父容器管理含有子组件的Module，这样父组件就知道如何创建子组件对象了。

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
使用也很简单直接父组件中获取到子组件对象，这样就可使用子组件操作其管理的对象了。

###### 2、生命周期管理

我们知道使用父容器时一般在application的onCreate种提供容器的实例，这样这个容器就是整个应用唯一的实例。 子组件也类似，在使用子组件时则一般在Activity中提供子组件实例的获取，这样这个子容器activity生命周期内唯一。

Dagger的作用域有如下规定:

(1)如果某个类标记有作用域注解，该类就只能由带有相同作用域注解的组件使用。

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
