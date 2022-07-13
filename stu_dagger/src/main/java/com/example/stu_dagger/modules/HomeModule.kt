package com.example.stu_dagger.modules

import com.example.stu_dagger.presenter.HomePresenter
import com.example.stu_dagger.presenter.HomePresenterImpl
import com.example.stu_dagger.service.ApiService
import dagger.Module
import dagger.Provides

/**
 * Create by SunnyDay /07/13 21:43:19
 */
@Module
class HomeModule {
    @Provides
    fun providerHomePresenter(api: ApiService):HomePresenter{
        val result = HomePresenterImpl(api)
        println("HomeModule:result1")
        return result
    }

    @Provides
    fun providerHomePresenter(api: ApiService,s:String):HomePresenter{
        val result2 = HomePresenterImpl(api)
        println("HomeModule:result2")
        return result2
    }

    @Provides
    fun providerString():String{
        return "s"
    }

}