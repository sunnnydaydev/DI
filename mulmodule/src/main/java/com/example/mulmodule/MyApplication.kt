package com.example.mulmodule

import android.app.Application


/**
 * Create by SunnyDay /12/10 13:56:09
 */
class MyApplication : Application() {

    private val container: AppContainer = DaggerAppContainer.create()
    override fun onCreate() {
        super.onCreate()
    }

    fun getAppComponent () = container
}