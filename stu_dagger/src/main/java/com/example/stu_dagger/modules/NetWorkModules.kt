package com.example.stu_dagger.modules

import com.example.stu_dagger.service.ApiService
import dagger.Module
import dagger.Provides
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
    fun provideLoginRetrofitService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .build()
            .create(ApiService::class.java)
    }
}