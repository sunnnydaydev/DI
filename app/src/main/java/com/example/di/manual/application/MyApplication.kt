package com.example.di.manual.application

import android.app.Application
import com.example.di.dagger_basic.container.ApplicationComponent
import com.example.di.dagger_basic.container.DaggerApplicationComponent
import com.example.di.manual.container.AppContainer

/**
 * Create by SunnyDay 2022/06/27 17:33:57
 */
class MyApplication:Application() {
    private val applicationComponent  by lazy {
       DaggerApplicationComponent.create()
    }
    val container = AppContainer()
    override fun onCreate() {
        super.onCreate()
    }
    fun getDaggerContainer(): ApplicationComponent = applicationComponent
}