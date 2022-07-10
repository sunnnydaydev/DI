package com.example.stu_dagger.components

import com.example.stu_dagger.repo.UserRepository
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /07/07 21:32:57
 */
@Singleton
@Component
interface ApplicationComponent {
    fun getUserRepository():UserRepository
}