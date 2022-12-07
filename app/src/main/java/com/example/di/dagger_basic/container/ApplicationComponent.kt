package com.example.di.dagger_basic.container


import com.example.di.dagger_basic.repository.UserRepository
import com.example.di.dagger_basic.test.Car
import dagger.Component

/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component
interface ApplicationComponent {
     fun  getUserRepository():UserRepository
     fun  getCar():Car
}