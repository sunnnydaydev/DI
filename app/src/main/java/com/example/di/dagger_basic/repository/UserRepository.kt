package com.example.di.dagger_basic.repository

import com.example.di.dagger_basic.anno.MakeSingleTon
import com.example.di.dagger_basic.service.LoginRetrofitService
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Create by SunnyDay 2022/07/01 10:59:10
 */
@MakeSingleTon
class UserRepository @Inject constructor(
    val localDataSource: UserLocalDataSource,
    val remoteDataSource: UserRemoteDataSource
)

class UserLocalDataSource @Inject constructor() {}

class UserRemoteDataSource @Inject constructor() {}