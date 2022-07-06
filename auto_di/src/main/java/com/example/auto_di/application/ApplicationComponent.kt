package com.example.auto_di.application

import com.example.auto_di.net.module.NetWorkModule
import com.example.auto_di.net.module.SubcomponentsModule
import com.example.auto_di.ui.LoginActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /07/03 20:10:47
 */
@Singleton
@Component(modules = [NetWorkModule::class, SubcomponentsModule::class])
interface ApplicationComponent {
    fun loginComponent(): LoginComponent.Factory

    fun
}