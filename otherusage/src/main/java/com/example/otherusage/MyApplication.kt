package com.example.otherusage

import android.app.Application
import com.example.otherusage.dagger.AppComponent
import com.example.otherusage.dagger.DaggerAppComponent
import com.example.otherusage.dagger.ProvidersModule

/**
 * Create by SunnyDay /12/24 19:18:58
 */
class MyApplication : Application() {

    private val appComponent: AppComponent = DaggerAppComponent.builder()
        .providersModule(ProvidersModule())
        .build()

    override fun onCreate() {
        super.onCreate()
    }

    fun getContainer() = appComponent
}