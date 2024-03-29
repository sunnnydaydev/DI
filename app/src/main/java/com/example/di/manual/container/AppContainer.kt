package com.example.di.manual.container

import com.example.di.manual.repository.UserLocalDataSource
import com.example.di.manual.repository.UserRemoteDataSource
import com.example.di.manual.repository.UserRepository
import com.example.di.manual.service.LoginService
import retrofit2.Retrofit

/**
 * Create by SunnyDay 2022/06/27 17:31:21
 */
class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.baidu.com") // 测试使用，这里就当做访问后台登录接口了。
        .build()
        .create(LoginService::class.java)
    private val remoteDataSource = UserRemoteDataSource(retrofit)
    private val localDataSource = UserLocalDataSource()

    // 容器管理
    val userRepository = UserRepository(localDataSource, remoteDataSource)
    var loginContainer: LoginContainer? = null
}
