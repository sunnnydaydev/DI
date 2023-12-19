package com.example.daggerreview

import com.example.daggerreview.login.UserInfo
import com.example.daggerreview.net.ApiService
import dagger.Subcomponent

/**
 * Create by SunnyDay /12/19 21:20:10
 */

@Subcomponent
interface LoginComponent{

    fun getApiService():ApiService
    fun getUserInfo():UserInfo
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}
