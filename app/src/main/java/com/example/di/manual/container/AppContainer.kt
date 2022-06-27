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
        .baseUrl("https://www.baidu.com")
        .build()
        .create(LoginService::class.java)
    private val remoteDataSource = UserRemoteDataSource(retrofit)
    private val localDataSource = UserLocalDataSource()
    val userRepository = UserRepository(localDataSource, remoteDataSource)

}