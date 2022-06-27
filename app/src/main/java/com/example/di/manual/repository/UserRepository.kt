package com.example.di.manual.repository

import com.example.di.manual.service.LoginService

/**
 * Create by SunnyDay 2022/06/27 16:17:13
 */
class UserRepository(  val localDataSource: UserLocalDataSource,
                       val remoteDataSource: UserRemoteDataSource) {
}

class UserRemoteDataSource (  val loginService: LoginService){

}

class UserLocalDataSource {

}
