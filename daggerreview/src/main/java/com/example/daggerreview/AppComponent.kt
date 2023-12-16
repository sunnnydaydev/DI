package com.example.daggerreview

import com.example.daggerreview.entity.UserRepository
import com.example.daggerreview.net.NetModule
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /12/14 21:28:46
 */

@Component(modules = [NetModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)
}