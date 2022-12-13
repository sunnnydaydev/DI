package com.example.di.dagger_basic.modules

import com.example.di.dagger_basic.anno.FragmentKey
import com.example.di.dagger_basic.repository.Fragment
import com.example.di.dagger_basic.repository.HomeFragment
import com.example.di.dagger_basic.repository.ProfileFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Create by SunnyDay /12/13 21:24:58
 */
@Module
class FragmentModule {
    @Provides
    @IntoMap
    @FragmentKey(HomeFragment::class)
    fun providerHomeFragment() = HomeFragment()

    @Provides
    @IntoMap
    @FragmentKey(HomeFragment::class)
    fun providerProfileFragment() = ProfileFragment()
}