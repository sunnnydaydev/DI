package com.example.di.dagger_basic.modules

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

/**
 * Create by SunnyDay /12/08 22:56:07
 */
@Module
class TestModule {
    @Provides
    fun providerOkhttpClient(): OkHttpClient{
        println("providerOkhttpClient")
       return OkHttpClient.Builder().build()
    }
}