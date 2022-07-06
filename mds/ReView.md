# Dagger基础

###### 1、依赖引入

app/build.gradle 添加下依赖

```groovy

    apply plugin: 'kotlin-kapt'

    dependencies {
        implementation 'com.google.dagger:dagger:2.38.1'
        kapt 'com.google.dagger:dagger-compiler:2.38.1'
    }
```
###### 2、要使用的类

![Login自动注入](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/di/userrepo.png)


- UserRepository是数据仓库管理类，负责管理UserLocalDataSource和UserRemoteDataSource

- UserLocalDataSource负责管理本地数据

- UserRemoteDataSource负责管理远程数据

三者可构成依赖关系，UserRepository依赖UserLocalDataSource和UserRemoteDataSource，用代码表示如下：

```kotlin
/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
)
class UserLocalDataSource
class UserRemoteDataSource
```

###### 2、如何自动生成实例

      先上栗子 后分析流程、再看生成代码。
      @Inject

###### 3、通过容器管理

      @Conponent

###### 4、单例

