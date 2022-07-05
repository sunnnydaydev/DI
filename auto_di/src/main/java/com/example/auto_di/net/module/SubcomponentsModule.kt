package com.example.auto_di.net.module

import com.example.auto_di.application.LoginComponent
import dagger.Module

/**
 * Create by SunnyDay /07/05 20:44:02
 */
@Module(subcomponents = [LoginComponent::class])
class SubcomponentsModule {}