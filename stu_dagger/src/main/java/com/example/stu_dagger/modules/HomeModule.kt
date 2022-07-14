package com.example.stu_dagger.modules

import com.example.stu_dagger.presenter.HomePresenter
import com.example.stu_dagger.presenter.HomePresenterImpl
import com.example.stu_dagger.service.ApiService
import dagger.Binds
import dagger.Module
import dagger.Provides


/**
 * Create by SunnyDay /07/13 21:43:19
 */
@Module
abstract class HomeModule {
//    二者共存时：错误: A @Module may not contain both non-static and abstract binding methods
//    @Provides
//    fun providerHomePresenter(api: ApiService): HomePresenter {
//        return HomePresenterImpl(api)
//    }
    @Binds
    abstract fun bindHomePresenter(homePresenterImp: HomePresenterImpl): HomePresenter
}