package com.example.di.dagger_basic.container

import com.example.di.dagger_basic.anno.MakeSingleTon
import com.example.di.dagger_basic.repository.UserRepository
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@MakeSingleTon
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}