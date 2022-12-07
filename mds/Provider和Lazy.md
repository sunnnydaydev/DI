
# Provider 和Lazy

直接注入，Factory方式，Provider方式，Lazy方式的区别~

###### 1、Provider

最直观的结论是：

- 直接注入的方式每次获取到的依赖对象相同，通过Provider< T >注入的方式每次获取的依赖对象T都是新的对象。
- 直接注入的方式的对象在编译时就创建好了，通过Provider< T >注入的方式的对象在使用时才创建。

官方文档给的解释->

Provides instances of T,you can also inject Provider<T>. Compared to injecting T directly, injecting Provider<T> enables:

- retrieving multiple instances.
- lazy or optional retrieval of an instance.
- breaking circular dependencies.
- abstracting scope so you can look up an instance in a smaller scope from an instance in a containing scope.

验证下 ->

```kotlin
//以Provider<T>方式注入
class Car @Inject constructor(val seatProvider: Provider<Seat>)
class Seat @Inject constructor(){
    init {
        println("test-Seat Init")
    }
}
```

```kotlin
//直接注入依赖对象
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource
)
class UserLocalDataSource @Inject constructor(){
    init {
        println("test-UserLocalDataSource Init")
    }
}
```

```kotlin
@Component
interface ApplicationComponent {
     fun  getUserRepository():UserRepository
     fun  getCar():Car
}
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val repo = daggerContainer.getUserRepository()
        val car = daggerContainer.getCar()

        lifecycleScope.launch (Dispatchers.IO){
            delay(5000)
            println("test-localDataSource1:${repo.localDataSource}")
            println("test-localDataSource2:${repo.localDataSource}")
            println("test-seat1:${car.seatProvider.get()}")
            println("test-seat2:${car.seatProvider.get()}")
        }
    }
}
```

- 2022-12-07 21:46:51.825 I/System.out: test-UserLocalDataSource Init
- 2022-12-07 21:46:56.835 I/System.out: test-localDataSource1:com.example.di.dagger_basic.repository.UserLocalDataSource@587680d
- 2022-12-07 21:46:56.835 I/System.out: test-localDataSource2:com.example.di.dagger_basic.repository.UserLocalDataSource@587680d
- 2022-12-07 21:46:56.835 I/System.out: test-Seat Init
- 2022-12-07 21:46:56.836 I/System.out: test-seat1:com.example.di.dagger_basic.test.Seat@36973c2
- 2022-12-07 21:46:56.836 I/System.out: test-Seat Init
- 2022-12-07 21:46:56.836 I/System.out: test-seat2:com.example.di.dagger_basic.test.Seat@f3795d3

对比localDataSource1 localDataSource2 和seat1 seat2的值可发现直接注入的每次获取到的实例相同，通过Provider< T >注入的每次获取的实例不同。

对比UserLocalDataSource初始化时间可知Provider< T >注入方式是惰性初始化T对象的，对象T在使用时才初始化。

源码验证下实例的获取区别 ->

```java
// 删除了无关代码，只看核心：
public final class DaggerApplicationComponent implements ApplicationComponent {
    /**
     * 目标类UserRepository的 创建方式为new，依赖项的创建方式也是new 
     * */
  @Override
  public UserRepository getUserRepository() {
    return new UserRepository(new UserLocalDataSource());
  }

  /**
   * 目标类的创建方式是new，依赖项的创建方式是Seat_Factory.create：
   * 
   * 1、Seat_Factory提供了两种方式创建Seat对象，一种是newInstance，一种是create-get,由于Car的构造要求Provider<Seat>
   * 这里只能以create的方式。  
   * 
   * 2、get的底层调用的是newInstance，每次newInstance 都会new新的对象。
   * 
   * 3、详情可参考Seat_Factory代码分析
   * */
  @Override
  public Car getCar() {
    return new Car(Seat_Factory.create());
  }
  
}
```
###### 2、Lazy

栗子很简单吧上面的Provider改为Dagger的Lazy

```kotlin
class Car @Inject constructor( val seatProvider: Lazy<Seat>)
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val repo = daggerContainer.getUserRepository()
        val car = daggerContainer.getCar()

        lifecycleScope.launch (Dispatchers.IO){
            delay(5000)
            println("test-localDataSource1:${repo.localDataSource}")
            println("test-localDataSource2:${repo.localDataSource}")
            println("test-seat1:${car.seatProvider.get()}")
            println("test-seat2:${car.seatProvider.get()}")
        }
    }
}
```

- 2022-12-07 22:33:27.083  I/System.out: test-UserLocalDataSource Init
- 2022-12-07 22:33:32.093  I/System.out: test-localDataSource1:com.example.di.dagger_basic.repository.UserLocalDataSource@587680d
- 2022-12-07 22:33:32.093  I/System.out: test-localDataSource2:com.example.di.dagger_basic.repository.UserLocalDataSource@587680d
- 2022-12-07 22:33:32.093  I/System.out: test-Seat Init
- 2022-12-07 22:33:32.093  I/System.out: test-seat1:com.example.di.dagger_basic.test.Seat@36973c2
- 2022-12-07 22:33:32.093  I/System.out: test-seat2:com.example.di.dagger_basic.test.Seat@36973c2

对比时间可知Lazy延迟初始化了

Lazy方式创建的对象每次获取的都是同个。这点与直接注入一样。

###### 3、Factory
