package com.carry.direview.dagger

import com.carry.direview.entity.UserRepository
import dagger.Component

/**
 * Create by SunnyDay /03/04 21:32:50
 */
@Component
interface ApplicationComponent {
    fun getUserRepository(): UserRepository
}