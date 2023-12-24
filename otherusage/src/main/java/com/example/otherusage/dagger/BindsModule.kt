package com.example.otherusage.dagger

import com.example.otherusage.entity.Animal
import com.example.otherusage.entity.Dog
import dagger.Binds
import dagger.Module

/**
 * Create by SunnyDay /12/24 19:25:44
 */

@Module
interface BindsModule {

    @Binds
    fun bindsAnimal(dog:Dog):Animal
}