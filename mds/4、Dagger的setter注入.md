# Dagger的setter注入

安卓中，Activity的创建不是由开发者控制的，由系统的AMS负责创建、管理。这时我们便不能通过构造方法进行字段注入了，恰巧Dagger提供了相应的注入
方式。

```kotlin
/**
 * Create by SunnyDay /12/14 22:13:28
 * mock the android viewModel for test notice it is not a real viewModel
 */
class MainViewModel @Inject constructor()
```

```kotlin
/**
 * Create by SunnyDay /12/14 21:28:46
 */

@Component
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    /**
     * 提供要注入的类，注意这里不能写Base类。如这里写个AbsActivity，然后为所有的activity去注入字段这种做法行不通。
     * */
    fun injectMainActivity(activity: MainActivity)
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    @Inject
    lateinit var vm: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("My test", "viewModel:${vm}")
        //viewModel:com.example.daggerreview.vm.MainViewModel@7b12f1f
    }
}
```

可见十分简单：定义个注入方法，然后在对应activity onCreate方法中注入下，最后就可以使用定义的字段了。但需要注意如下几点：

- 字段的访问权限不能为private的，否则Dagger报错。
- 若有多个activity需要注入不能共用一个注入方法。

接下来看下源码里面是怎样实现的？

/build/generated/source/kapt/debug 下 ->

```java

public final class DaggerAppComponent {
  private DaggerAppComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static AppComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    public AppComponent build() {
      return new AppComponentImpl();
    }
  }

  private static final class AppComponentImpl implements AppComponent {
    private final AppComponentImpl appComponentImpl = this;

    private Provider<UserRemoteDataSource> userRemoteDataSourceProvider;

    private AppComponentImpl() {

      initialize();

    }

    @SuppressWarnings("unchecked")
    private void initialize() {
      this.userRemoteDataSourceProvider = DoubleCheck.provider(UserRemoteDataSource_Factory.create());
    }

    @Override
    public UserRepository getUserRepository() {
      return new UserRepository(userRemoteDataSourceProvider.get());
    }

    /**
     * 1、实现接口方法
     * */
    @Override
    public void injectMainActivity(MainActivity activity) {
      injectMainActivity2(activity);
    }

    /**
     * 2、调用生成类MainActivity_MembersInjector的injectVm进行注入
     * */
    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectVm(instance, new MainViewModel());
      return instance;
    }
  }
}

```

```java
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<MainViewModel> vmProvider;

  public MainActivity_MembersInjector(Provider<MainViewModel> vmProvider) {
    this.vmProvider = vmProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<MainViewModel> vmProvider) {
    return new MainActivity_MembersInjector(vmProvider);
  }

  /**
   * 3、具体的注入细节
   * */
  @Override
  public void injectMembers(MainActivity instance) {
    injectVm(instance, vmProvider.get());
  }

  /**
   * 4、最终给对象的成员进行初始化
   * */
  @InjectedFieldSignature("com.example.daggerreview.MainActivity.vm")
  public static void injectVm(MainActivity instance, MainViewModel vm) {
    instance.vm = vm;
  }
}
```