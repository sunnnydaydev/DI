package com.example.di.dagger_basic.container


import com.example.di.dagger_basic.DaggerBasicActivity
import com.example.di.dagger_basic.ILoginPresent
import com.example.di.dagger_basic.LoginPresent
import com.example.di.dagger_basic.modules.BindsModule
import com.example.di.dagger_basic.modules.FragmentModule
import com.example.di.dagger_basic.modules.ProviderModule
import com.example.di.dagger_basic.modules.TestModule
import com.example.di.dagger_basic.repository.UserRepository
import com.example.di.dagger_basic.test.Car
import dagger.Component

/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component(modules = [FragmentModule::class])
interface ApplicationComponent {
      fun injectMap(activity: DaggerBasicActivity)
}