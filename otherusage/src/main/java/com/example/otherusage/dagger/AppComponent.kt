package com.example.otherusage.dagger

import android.app.Application
import com.example.otherusage.entity.Animal
import com.example.otherusage.entity.Cat
import dagger.BindsInstance
import dagger.Component

/**
 * Create by SunnyDay /12/24 19:19:46
 */

@Component(modules = [ProvidersModule::class, BindsModule::class])
interface AppComponent {
    fun getAnimal(): Animal

    fun getCat(): Cat

    fun application():Application

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance application: Application):AppComponent
    }
}