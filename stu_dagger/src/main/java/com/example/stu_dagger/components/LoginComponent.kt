package com.example.stu_dagger.components

import com.example.stu_dagger.LoginActivity
import dagger.Subcomponent

/**
 * Create by SunnyDay /07/16 17:57:08
 */
@Subcomponent
interface LoginComponent {
    fun inject(activity:LoginActivity)

    // 提供创建子容器对象的接口，这样父容器知道如何创建子容器对象。
    @Subcomponent.Factory
    interface Factory{
        fun create():LoginComponent
    }
}