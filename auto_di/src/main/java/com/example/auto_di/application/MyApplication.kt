package com.example.auto_di.application

import android.app.Application

/**
 * Create by SunnyDay /07/03 20:16:16
 */
class MyApplication:Application() {
    val component: ApplicationComponent = DaggerApplicationComponent.create()
    override fun onCreate() {
        super.onCreate()
    }
}