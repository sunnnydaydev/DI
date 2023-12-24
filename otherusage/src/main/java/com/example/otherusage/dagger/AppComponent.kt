package com.example.otherusage.dagger

import com.example.otherusage.entity.Animal
import com.example.otherusage.entity.Cat
import dagger.Component

/**
 * Create by SunnyDay /12/24 19:19:46
 */

@Component(modules = [ProvidersModule::class, BindsModule::class])
interface AppComponent {
    fun getAnimal(): Animal

    fun getCat(): Cat
}