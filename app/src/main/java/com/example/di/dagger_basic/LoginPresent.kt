package com.example.di.dagger_basic

import com.example.di.dagger_basic.service.LoginRetrofitService
import javax.inject.Inject

/**
 * Create by SunnyDay /12/08 21:17:43
 */
class LoginPresent @Inject constructor(private val loginRetrofitService: LoginRetrofitService):ILoginPresent {
    override fun login() {
        loginRetrofitService.login()
    }
}