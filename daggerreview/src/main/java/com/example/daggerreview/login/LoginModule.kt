package com.example.daggerreview.login

import com.example.daggerreview.LoginComponent
import dagger.Module

/**
 * Create by SunnyDay /12/19 21:22:37
 */

/**
 * 子容器被Module所管理，这里创建一个LoginModule，通过subcomponents制定所管理的子容器
 * */
@Module(subcomponents = [LoginComponent::class])
class LoginModule
