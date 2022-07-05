package com.example.auto_di.application

import com.example.auto_di.ui.LoginActivity
import dagger.Subcomponent

/**
 * Create by SunnyDay /07/05 20:28:04
 */
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)

    //必须：再定义个子组件Factory，便于ApplicationComponent知道如何创建LoginComponent实例
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}