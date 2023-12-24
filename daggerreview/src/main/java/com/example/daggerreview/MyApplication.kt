package com.example.daggerreview

import android.app.Application

/**
 * Create by SunnyDay /12/14 21:28:32
 */
class MyApplication : Application() {
    private lateinit var appContainer: AppComponent
    override fun onCreate() {
        super.onCreate()
        appContainer = DaggerAppComponent.builder().build()
    }

    fun getContainer() = appContainer
}