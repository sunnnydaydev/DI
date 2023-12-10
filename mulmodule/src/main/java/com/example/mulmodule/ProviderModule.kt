package com.example.mulmodule

import com.example.loginmodule.dagger.DaggerLoginContainer
import com.example.loginmodule.dagger.LoginContainer
import dagger.Module
import dagger.Provides

/**
 * Create by SunnyDay /12/10 15:28:08
 */

@Module
class ProviderModule {
    @Provides
    fun providerLoginContainer():LoginContainer = DaggerLoginContainer.create()
}