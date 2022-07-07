package com.example.stu_dagger.repo

import javax.inject.Inject

/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
)