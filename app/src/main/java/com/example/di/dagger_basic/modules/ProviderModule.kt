package com.example.di.dagger_basic.modules

import com.example.di.dagger_basic.repository.Fragment
import com.example.di.dagger_basic.repository.HomeFragment
import com.example.di.dagger_basic.repository.ProfileFragment
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
class ProviderModule{

    @Provides
    fun provideHomeFragment() = HomeFragment()

    @Provides
    fun provideProfileFragment() = ProfileFragment()

    @Provides
    fun provideFragment() = Fragment()
}