# Dagger的Module

当一个字段的类型为接口或抽象类时我们是无法使用@Inject注解提供对象的，还有一种情况android中常见的一些类库的对象创建也不是直接new出来的
如OkHttp、Retrofit等此时Dagger Module就派上用场了。

本节将会了解到连个重要的注解

- @Provider：告知 Dagger 如何提供您的项目所不具备的类的实例。
- @Binds：告知Dagger 接口应采用哪种实现。

# @Provider

[Dagger的setter注入](./4、Dagger的setter注入.md)这里我们了解到如何使用Setter注入方式为Activity注入VM，接下来有个常见的需求VM中调用网络服务获取数据
我们代码可能这样写：

创建Retrofit Service 接口
```kotlin
interface ApiService {
    @GET("/")
    fun getData():Call<ResponseBody>
}
```

VM中进行服务调用处理

```kotlin
class MainViewModel @Inject constructor(private val apiService: ApiService) {
    fun getSeverData() {
        apiService.getData().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("My test", "onResponse：${response.isSuccessful}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("My test", "onFailure：response is not Successful ")
            }

        })
    }
}
```
ui层进行调用

```kotlin
class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }

    @Inject
    lateinit var vm: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.getSeverData()
    }
}
```

此时我们run一下会发现->

```kotlin
ApiService cannot be provided without an @Provides-annotated method
```
报错啦，回顾一下代码我们会发现VM的成员ApiService没有提供实例，因此编译报错，由于ApiService的特殊性不能直接new对象，我们无法使用@Inject
为其提供对象这里就以@Provides的方式提供下。

###### 1、使用@Provides提供对象

```kotlin
@Module
class NetModule {
    @Provides
    fun providerApiService() =
        Retrofit.Builder().baseUrl("https://www.baidu.com").build().create(ApiService::class.java)
}
```
很简单创建一个类@Module标记，这样Dagger知道这是一个Module，然后module中提供方法创建实例，如上提供了返回值类型是ApiService的方法，方法使用@Provides标记。

完成上面的步骤后还是不行的，这个module要受Dagger容器管理这样在VM需要ApiService的值时Dagger会找到这里的对象。因此我们要在容器中注册下这个module

```kotlin
@Component(modules = [NetModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)
}
```
通过modules = [NetModule::class] 向Dagger容器注册了NetModule，这里要是注册多个容器时逗号隔开即可。

这样再run一下，我们在VM中就能看到log了->

My test  D  onResponse：true

###### 2、module的方法需要参数时怎么办？

我们可能碰到这样的需求，如上给retrofit传递自定义的okhttp实例，于是我们的方法现在变成了这样：

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
}
```
此时我们再run一下会发现:

okhttp3.OkHttpClient cannot be provided without an @Inject constructor or an @Provides-annotated method.

又是编译错误，看着错误提示解决方案很明显

（1）使用@Inject标记OkHttpClient类的构造

（2）使用@Provides标记module的方法

方案1是行不通的，具体的原因我们前面已经了解了，接下来我们就用方案2来解决一下：

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

甚是简单在这个NetModule中提供一个方法来创建OkHttpClient的实例即可，然后以@Provides标记下方法，这样Dagger在providerApiService中使用OkHttpClient
时就能自动寻找到

###### 3、一些想法感悟

- @Inject注解的类默认受Dagger容器管理
- Module注册到Dagger容器后Module管理的对象也受Dagger容器管理
- 受同一个Dagger容器管理的对象之间可以相互调用

前两点还是很好理解的，第三点我们来回顾下MainViewModule,这里使用@Inject标记了其构造，然后ApiService的对象就直接从Module中取到了。

我们还可以写代码验证下Module中@Provides标记的方法需要的实例可取自@Inject注解标记的类：

```kotlin
data class UserRepository @Inject constructor(val userRemoteDataSource: UserRemoteDataSource)
```

```kotlin
    @Provides
    fun providerTest(repository: UserRepository) = repository
```

接着我们还能更大胆的想下假如我有个BaseLib gradle模块，app 工程依赖这个模块，这里有个Module也受Dagger容器管理,那么Dagger容器管理对象还能互相调用吗？

```kotlin
//1、base_lib gradle module 定义
package com.example.base_lib.entity
import javax.inject.Inject
/**
 * Create by SunnyDay /12/18 21:19:14
 */
class User @Inject constructor()
```

```kotlin
class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }

    @Inject
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 2、app 模块使用
        Log.d("my test","user hash code-> $user")
        //user hash code-> com.example.base_lib.entity.User@1c3e317
    }

}
```

# @Binds

我们在base_lib 定义如下entity

```kotlin
interface Person
class User @Inject constructor()
class Man:Person
```

然后这样使用：

```kotlin
class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    
    @Inject
    lateinit var person: Person

    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.getSeverData()

        Log.d("my test","person hash code-> $person")
    }
    
}
```

Person cannot be provided without an @Provides-annotated method.

编译直接报错了，按照提示我们需要以@Provides方式提供，我们按照这种方式试试

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

    /**
     * 1、返回值类型定义为接口类型Person（重点）
     * 2、返回值给一个实现类。
     * */
    @Provides
    fun providerPerson():Person = Man()
}
```

除了这种方式还有其他的方式吗？答案是有的看看@Binds怎样实现的

```kotlin
@Module
interface PersonModule {
    @Binds
    fun bindManModule(man: Man):Person
}
```

```kotlin
interface Person
class User @Inject constructor()
class Man @Inject constructor():Person
```

- 这里定义了一个接口
- 接口内部使用@Binds标记接口方法
- 方法的返回值类型为目标基类类型
- 方法的参数为具体的实现类（注意方法的参数也要提供实例，这里会自动寻找class Man @Inject constructor():Person）

然后Module指定为容器管理

```kotlin
@Component(modules = [NetModule::class,PersonModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)
}
```

这样再次run一下就会发现跑起来了~










