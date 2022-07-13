package com.example.stu_dagger.components

import com.example.stu_dagger.MainActivity
import com.example.stu_dagger.modules.HomeModule
import com.example.stu_dagger.modules.NetWorkModules
import com.example.stu_dagger.presenter.HomePresenter
import com.example.stu_dagger.repo.UserRepository
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /07/07 21:32:57
 */
@Singleton
@Component(modules = [NetWorkModules::class,HomeModule::class])
interface ApplicationComponent {

    fun getUserRepository():UserRepository

    fun inject(activity:MainActivity)
}