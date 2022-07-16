package com.example.stu_dagger.modules

import com.example.stu_dagger.components.LoginComponent
import dagger.Module

/**
 * Create by SunnyDay /07/16 18:05:08
 */
@Module(subcomponents = [LoginComponent::class] )
class SubcomponentsModule