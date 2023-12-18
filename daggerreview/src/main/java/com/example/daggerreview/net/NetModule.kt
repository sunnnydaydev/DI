package com.example.daggerreview.net

import com.example.base_lib.entity.Man
import com.example.base_lib.entity.Person
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Create by SunnyDay /12/16 18:04:00
 */

@Module
class NetModule {

    @Provides
    fun providerApiService(client:OkHttpClient): ApiService =
        Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(client)
            .build()
            .create(ApiService::class.java)

    @Provides
    fun providerOkHttpClient():OkHttpClient = OkHttpClient.Builder().build()
}