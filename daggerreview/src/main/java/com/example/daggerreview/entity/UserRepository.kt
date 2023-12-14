package com.example.daggerreview.entity

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by SunnyDay /12/14 21:24:54
 */

data class UserRepository @Inject constructor(val userRemoteDataSource: UserRemoteDataSource)