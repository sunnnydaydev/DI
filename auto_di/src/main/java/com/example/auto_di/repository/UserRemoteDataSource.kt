package com.example.auto_di.repository

import com.example.auto_di.net.service.LoginRetrofitService
import javax.inject.Inject

/**
 * Create by SunnyDay /07/03 20:24:55
 */
data class UserRemoteDataSource @Inject constructor(val loginService: LoginRetrofitService) {}