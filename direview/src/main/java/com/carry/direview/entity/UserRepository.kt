package com.carry.direview.entity

import javax.inject.Inject

/**
 * Create by SunnyDay /03/04 20:47:18
 */
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
)
class UserLocalDataSource @Inject constructor()
class UserRemoteDataSource @Inject constructor()
