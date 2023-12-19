package com.example.daggerreview

import com.example.base_lib.entity.Person
import com.example.base_lib.entity.PersonModule
import com.example.base_lib.entity.User
import com.example.daggerreview.entity.UserRepository
import com.example.daggerreview.login.LoginModule
import com.example.daggerreview.net.NetModule
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /12/14 21:28:46
 */

@Component(modules = [NetModule::class,PersonModule::class,LoginModule::class])
@Singleton
interface AppComponent {
    fun getUserRepository(): UserRepository

    fun injectMainActivity(activity: MainActivity)

    fun getLoginComponent():LoginComponent.Factory
}