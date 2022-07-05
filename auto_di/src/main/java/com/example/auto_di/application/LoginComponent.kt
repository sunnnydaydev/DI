package com.example.auto_di.application

import com.example.auto_di.anno.ActivityScope
import com.example.auto_di.ui.LoginActivity
import com.example.auto_di.ui.LoginPasswordFragment
import com.example.auto_di.ui.LoginUsernameFragment
import dagger.Subcomponent

/**
 * Create by SunnyDay /07/05 20:28:04
 */
@ActivityScope
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)
    fun inject(usernameFragment: LoginUsernameFragment)
    fun inject(passwordFragment: LoginPasswordFragment)

    //必须：再定义个子组件Factory，便于ApplicationComponent知道如何创建LoginComponent实例
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}