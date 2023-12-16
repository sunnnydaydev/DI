package com.example.daggerreview.net

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

/**
 * Create by SunnyDay /12/16 18:04:00
 */

@Module
class NetModule {

    @Provides
    fun providerApiService() =
        Retrofit.Builder().baseUrl("https://www.baidu.com").build().create(ApiService::class.java)
}