package com.example.auto_di.repository

import com.example.auto_di.net.service.LoginRetrofitService
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by SunnyDay /07/03 20:24:55
 */
@Singleton
data class UserRemoteDataSource @Inject constructor(val loginService: LoginRetrofitService) {}