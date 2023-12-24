package com.example.otherusage.dagger

import com.example.otherusage.entity.Cat
import dagger.Module
import dagger.Provides

/**
 * Create by SunnyDay /12/24 19:24:20
 */

@Module
class ProvidersModule {
    @Provides
    fun providerCat() = Cat()
}