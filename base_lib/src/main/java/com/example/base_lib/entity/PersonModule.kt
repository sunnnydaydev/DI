package com.example.base_lib.entity

import dagger.Binds
import dagger.Module

/**
 * Create by SunnyDay /12/18 21:28:04
 */

@Module
interface PersonModule {
    @Binds
    fun bindManModule(man: Man):Person
}