package com.example.di.dagger_basic.modules

import com.example.di.dagger_basic.repository.UserBean
import com.example.di.dagger_basic.service.LoginRetrofitService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Create by SunnyDay /12/08 21:31:47
 */
@Module
class ProviderModule : BaseProviderModule() {
    /**
     * LoginRetrofitService对象的提供。
     *
     * 当Dagger创建某个类的对象时，发现此类没有被@Inject注解构造，则就去自己管理的Module中去找@Provides注解的方法。
     * 当方法的返回值符合时直接使用。
     * */
    @Provides
    fun provideLoginRetrofitService(client: OkHttpClient): LoginRetrofitService {
        return Retrofit.Builder()
            .baseUrl("https://a.bcom/login/")
            .client(client)
            .build()
            .create(LoginRetrofitService::class.java)
    }
// Cannot have more than one binding method with the same name in a single module

//    @Provides
//    fun provideLoginRetrofitService(userBean: UserBean): LoginRetrofitService {
//        // todo use userBean data here
//        return Retrofit.Builder()
//            .baseUrl("https://a.bcom/login/")
//            .build()
//            .create(LoginRetrofitService::class.java)
//    }

    @Provides
    fun provideUserBean():UserBean{
        println("provideUserBean-in module")
        return UserBean()
    }
}