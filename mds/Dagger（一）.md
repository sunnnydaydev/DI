# Dagger基础

可以大致回顾下"手动依赖注入"的步骤：

- 构建类的依赖关系：这个一般适用构造函数注入的方式

- 使用工厂模式来优化ViewModel的创建

- 创建个container容器管理数据

"手动依赖注入"随着项目的增大可能会出现问题，Dagger是一个自动依赖注入的框架，框架会根据配置自动生成代码，生成代码与您原本需要手动编写的代码相似，极大避免了
"手动依赖注入"容易引发的错误，自动代码生成还可以提升编码效率。


Dagger的大致流程就是需要我们声明依赖项并使用注解来指定如何满足类之间的依赖关系。Dagger 便会在构建时自动执行以上"手动依赖注入"所有步骤。


还是"登录流程"的栗子，这里使用Dagger实现下。

首先app/build.gradle 添加下依赖

```groovy

    apply plugin: 'kotlin-kapt'

    dependencies {
        implementation 'com.google.dagger:dagger:2.38.1'
        kapt 'com.google.dagger:dagger-compiler:2.38.1'
    }
```

###### 1、创建实例构建依赖项

```kotlin

/**
 * Create by SunnyDay 2022/07/01 10:59:10
 */
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
) {

}

class UserLocalDataSource @Inject constructor() {}

/**
   interface LoginRetrofitService {
      @GET("/")
      fun getDataFromBaidu(): Call<ResponseBody>
   }
   注意：若@Inject标注构造后，构造有参数时，参数也需要相应的实例提供。否则如下：
   class UserRemoteDataSource @Inject constructor( val loginService: LoginRetrofitService) {}
   报错：LoginRetrofitService cannot be provided without an @Provides-annotated method.
   Dagger 不知道如何为LoginRetrofitService创建实例。
 * */
class UserRemoteDataSource @Inject constructor() {}
```

使用@Inject注解UserRepository的构造后，Dagger已经知道：

- 如何创建UserRepository的实例
- UserRepository的依赖项为UserLocalDataSource、UserRemoteDataSource。

但Dagger不知道如何创建其依赖项。如果您也为其他类添加了注释，Dagger 便会知道如何创建它们。

如上分别给UserLocalDataSource、UserRemoteDataSource类的构造也加上 @Inject注解即可。


###### 2、Dagger组件

在手动依赖注入中可以使用容器来管理UserRepository，Dagger中也定义了类似的注解。使用十分简单。

```kotlin
/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}
```

如上我们需要创建一个接口，并使用 @Component 为其添加注释。此时Dagger 会创建一个容器。在 @Component 接口内，您可以定义返回所需类（即 UserRepository）的实例的函数。
Dagger则知道如何为这个函数自动创建实例。

经过上述的定义后，我们就可以使用生成的代码了-->

```kotlin
package com.example.di.dagger_basic

class DaggerBasicActivity : AppCompatActivity() {
    companion object{
        const val  tag = "DaggerBasicActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)
        // 创建容器对象（DaggerApplicationComponent.Build().build()也可）
        val daggerApplicationComponent = DaggerApplicationComponent.create()
        // 获取实例对象
        val repository = daggerApplicationComponent.repository()
        val localDataSource = repository.localDataSource
        val remoteDataSource = repository.remoteDataSource
        Log.d(tag,"localDataSource:$localDataSource")
        Log.d(tag,"remoteDataSource:$remoteDataSource")
        /***
         * log:
         * 
         * UserLocalDataSource@3852713
         * UserRemoteDataSource@d4b9d50
         */
    }
}
```
注意下上述的细节：
- 生成的容器类是DaggerXXX，我们可以使用其create方法来创建实例。
- 容器内定义好方法或者字段即可自动生成对应实例。
  注意：
      方法返回值或者字段所属类已经定义好了依赖关系即可。
      其实定义字段也一样可以。
```kotlin
@Component
interface ApplicationComponent {
    val userRepositoryTest:UserRepository
}
```
```kotlin
  // 获取
  val repository = daggerApplicationComponent.userRepositoryTest
```

###### 3、单例的实现

业务中往往需要某些对象的实例是单例的这时可以使用@Singleton来实现。如容器中的UserRepository需要单例：

```kotlin
/**
 * 1、首先给容器添加单例注解
 * */
@Singleton
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}
```
```kotlin
/**
 * 2、对应实体类也加上单例注解即可。
 * */
@Singleton
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
) {}
```

注意容器ApplicationComponent对象不是单例的，我们可以把其的获取放到application中来达到单例的效果。

其实@Singleton为Dagger提供的一个单例注解，Dagger还可以让我们可以自定义注解同样可以达到单例的效果。

看看定义@Singleton如何定义的：
```kotlin
@Scope
@Documented
@Retention(RUNTIME)
public @interface Singleton {}
```
主要就是@Scope注解，Dagger定义了这个注解然后使用apt处理注解。

我们直接模仿定义个：
```kotlin
@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class MakeSingleTon
```

使用页简单替换之前的@Singleton即可：

```kotlin
@MakeSingleTon
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}
```
```kotlin
@MakeSingleTon
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
) {}
```

# 小结

总的来说了解了：

1、通过@Inject注解构造，知道了Dagger回构建依赖关系，创建实例

2、通过@Component注解一个接口则会生成一个容器，在容器中可直接定义具有依赖关系的类，容器自动创建对应实例。

3、使用@Single可获取单例，当然我们也可以自定义这个注解。

经过本章，了解下Dagger基础 ~






