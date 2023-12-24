package com.example.daggerreview

import android.util.Log
import com.example.daggerreview.dagger.ActivityScope
import com.example.daggerreview.entity.LoginViewModel
import com.example.daggerreview.login.UserInfo
import com.example.daggerreview.net.ApiService
import dagger.Subcomponent
import javax.inject.Singleton

/**
 * Create by SunnyDay /12/19 21:20:10
 */

@Singleton
@Subcomponent
interface LoginComponent {
    fun inject(activity: LoginActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }
}
