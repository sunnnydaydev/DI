package com.example.di.dagger_basic.modules

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntKey
import dagger.multibindings.IntoMap

/**
 * Create by SunnyDay /12/13 21:20:38
 */
@Module
class IntoMapModule {
    @Provides
    @IntoMap
    @IntKey(1)
    fun providerStudent1()="Tom"

    @Provides
    @IntoMap
    @IntKey(2)
    fun providerStudent2()="Kate"
}