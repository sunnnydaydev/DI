package com.example.stu_dagger

import android.app.Application
import com.example.stu_dagger.components.ApplicationComponent
import com.example.stu_dagger.components.DaggerApplicationComponent

/**
 * Create by SunnyDay /07/10 22:00:08
 */
class MyApplication :Application() {
    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
    override fun onCreate() {
        super.onCreate()
    }
}