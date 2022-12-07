package com.example.di.dagger_basic.container


import dagger.Component

/**
 * Create by SunnyDay 2022/07/01 11:56:49
 */
@Component
interface ApplicationComponent {
     fun  getViewModelFactory()
}