package com.example.di.dagger_basic.container


import com.example.di.dagger_basic.repository.UserRepository
import dagger.Component

/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component
interface ApplicationComponent {
     fun repository(): UserRepository
}