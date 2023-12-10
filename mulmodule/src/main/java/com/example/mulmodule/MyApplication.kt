package com.example.mulmodule

import android.app.Application
import com.example.loginmodule.dagger.LoginContainer
import com.example.loginmodule.dagger.ProviderLoginContainer


/**
 * Create by SunnyDay /12/10 13:56:09
 */
class MyApplication : Application(),ProviderLoginContainer {

    private val container: AppContainer = DaggerAppContainer.create()
    override fun onCreate() {
        super.onCreate()
    }

    fun getAppComponent () = container
    override fun providerLoginContainer(): LoginContainer = container.getLoginContainer()
}