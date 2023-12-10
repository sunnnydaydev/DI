package com.example.mulmodule

import com.example.loginmodule.dagger.DaggerLoginContainer
import com.example.loginmodule.dagger.LoginContainer
import dagger.Component

/**
 * Create by SunnyDay /12/10 13:58:34
 */

@Component(modules = [ProviderModule::class])
interface AppContainer{
    fun injectMainActivity(mainActivity: MainActivity)

    // 1、去除modules = [ProviderModule::class]
    // 2、fun getLoginContainer(): LoginContainer = DaggerLoginContainer.create()
    // 这种做法行不通
    fun getLoginContainer(): LoginContainer
}