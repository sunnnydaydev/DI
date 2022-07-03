package com.example.auto_di.repository

import javax.inject.Inject

/**
 * Create by SunnyDay /07/03 20:23:40
 */
data class UserRepository @Inject constructor(val userRemoteDataSource:UserRemoteDataSource)