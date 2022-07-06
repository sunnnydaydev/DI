package com.example.auto_di.net.module

import com.example.auto_di.net.service.LoginRetrofitService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Create by SunnyDay /07/03 20:39:57
 * 这里使用@Module+@Provides注解后，当依赖关系图中需要LoginRetrofitService实例时可直接自动从这里取。
 * 参见[com.example.auto_di.repository.UserRemoteDataSource],这个类构造需要LoginRetrofitService，但是LoginRetrofitService
 * 未使用@Inject注解自己的构造。
 */
@Module
class NetWorkModule {
    //注意这里标记方法上，而不是Module上
    @Singleton
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .build()
            .create(LoginRetrofitService::class.java)
    }


    @Singleton
    @Provides
    fun test(name:String): String {
        return ""
    }
}