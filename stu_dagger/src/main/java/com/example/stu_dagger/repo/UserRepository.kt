package com.example.stu_dagger.repo

/**
 * Create by SunnyDay /07/06 21:26:32
 */
class UserRepository(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
)