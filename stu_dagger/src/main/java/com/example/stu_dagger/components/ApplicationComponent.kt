package com.example.stu_dagger.components

import com.example.stu_dagger.repo.UserRepository
import dagger.Component

/**
 * Create by SunnyDay /07/07 21:32:57
 */
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
}