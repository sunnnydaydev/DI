# Dagger常见的注解

在Dagger基础中认识了几个注解：@inject、@Component、@Singleton，本章节就总结下其他的常用的注解。

- @Inject字段注入
- @Provider告知 Dagger 如何提供您的项目所不具备的类。
- @Module 模块的概念
- @Binds 告知 Dagger 接口应采用哪种实现
- @Subcomponent 子组件

###### 1、使用@Inject进行字段注入

安卓中，Activity的创建不是由开发者控制的，由系统的AMS负责创建、管理。这时我们便不能通过构造方法进行字段注入了，恰巧Dagger提供了相应的注入
方式。接下来看下Activity中如何自动注入loginPresenter的。

依赖关系很简单，Activity中持有loginPresenter字段。接下来看看Dagger是如何实现自动注入的。

（1）容器中定义个注入的方法。
```kotlin
/**
 * Create by SunnyDay /07/07 21:32:57
 */
@Singleton
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
    //核心api，为目标类的实例的字段注入值。
    fun inject(activity:MainActivity)
}
```
（2）使用

```kotlin
/**
 * Create by SunnyDay /07/11 20:35:29
 */
class LoginPresenter @Inject constructor(){}
```

```kotlin
class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        Log.d(tag,"loginPresent:$loginPresent")
    }
}
```
可见十分简单：定义个注入方法->在activity onCreate方法中注入下 ->定义的字段就有值了。

注意点：

- 字段的访问权限不能为private的。否则Dagger报错。
- 若有多个类不要注入时需要提供多个注入方法，不能共用一个注入方法。如SignUpActivity也需要注入字段，则我们应在容器中再定义个注入方法，传入的参数类型为SignUpActivity。
- 字段所属的类的构造上别忘了加@Inject，因为字段的value就是从生成类提供的api调用中获取的。

了解过XUtils或者APT的大概就可很快明白Dagger字段注入的原理。很明显Dagger的处理方式不是类似XUtils而是使用的APT。我们来看下相关的生成类：

- LoginPresenter的生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class LoginPresenter_Factory implements Factory<LoginPresenter> {
  @Override
  public LoginPresenter get() {
    return newInstance();
  }

  public static LoginPresenter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LoginPresenter newInstance() {
    return new LoginPresenter();
  }

  private static final class InstanceHolder {
    private static final LoginPresenter_Factory INSTANCE = new LoginPresenter_Factory();
  }
}
```
很简单，工厂方式创建LoginPresenter的对象，然后暴露get方法。上一节我们已经见过无参构造添加@Inject的，这种生成类是最简单的。

- Activity相关的生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})

// 泛型，类型就是我们的MainActivity类型。
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {

  private final Provider<LoginPresenter> loginPresentProvider;


  public MainActivity_MembersInjector(Provider<LoginPresenter> loginPresentProvider) {
    this.loginPresentProvider = loginPresentProvider;
  }
  
  public static MembersInjector<MainActivity> create(
      Provider<LoginPresenter> loginPresentProvider) {
    return new MainActivity_MembersInjector(loginPresentProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectLoginPresent(instance, loginPresentProvider.get());
  }

  //静态方法直接给目标对象的成员设置字段值。
  @InjectedFieldSignature("com.example.stu_dagger.MainActivity.loginPresent")
  public static void injectLoginPresent(MainActivity instance, LoginPresenter loginPresent) {
    instance.loginPresent = loginPresent;
  }
}
```
由于Activity的字段添加了@Inject注解，因此系统也为Activity生成了一个类。

- 容器生成类

```java
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerApplicationComponent implements ApplicationComponent {
    ...
    // 1、首先实现接口方法
  @Override
  public void inject(MainActivity activity) {
    injectMainActivity(activity);
  }
  //2、其次调用MainActivity_MembersInjector#injectLoginPresent对目标对象的字段进行注入。
  //   注入的值是直接new出来的。  
  private MainActivity injectMainActivity(MainActivity instance) {
    MainActivity_MembersInjector.injectLoginPresent(instance, new LoginPresenter());
    return instance;
  }

}
```

流程总结：

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/flow.png)
