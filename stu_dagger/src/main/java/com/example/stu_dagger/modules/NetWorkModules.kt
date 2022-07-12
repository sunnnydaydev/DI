package com.example.stu_dagger.modules

import com.example.stu_dagger.service.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Create by SunnyDay /07/12 21:06:50
 */
@Module
class NetWorkModules {
    /**
     * 提供Retrofit实例
     * */
    @Provides
    fun provideLoginRetrofitService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .client(client)
            .build()
            .create(ApiService::class.java)

    }

    @Provides
    fun providerOkhttpClient():OkHttpClient{
        return OkHttpClient()
        // return OkHttpClient.Builder().build() // 这样也可
    }
}