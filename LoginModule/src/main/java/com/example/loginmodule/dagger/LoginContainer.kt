package com.example.loginmodule.dagger

import com.example.loginmodule.entity.User
import com.example.loginmodule.ui.LoginActivity
import dagger.Component

/**
 * Create by SunnyDay /12/10 14:21:03
 */

@Component
interface LoginContainer {
    fun getUser():User

    fun  injectLoginActivity(loginActivity: LoginActivity)

    @Component.Builder
    interface Builder {
        fun build(): LoginContainer
    }

}