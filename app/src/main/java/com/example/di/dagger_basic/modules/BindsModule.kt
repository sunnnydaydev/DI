package com.example.di.dagger_basic.modules

import com.example.di.dagger_basic.ILoginPresent
import com.example.di.dagger_basic.LoginPresent
import dagger.Binds
import dagger.Module

/**
 * Create by SunnyDay /12/08 22:33:51
 */
@Module
abstract class BindsModule {
   @Binds
   abstract fun provideLoginPresent(loginPresent: LoginPresent): ILoginPresent
}