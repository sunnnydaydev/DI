package com.example.di.manual.application

import android.app.Application
import com.example.di.manual.container.AppContainer

/**
 * Create by SunnyDay 2022/06/27 17:33:57
 */
class MyApplication:Application() {
    val container = AppContainer()
    override fun onCreate() {
        super.onCreate()
    }
}