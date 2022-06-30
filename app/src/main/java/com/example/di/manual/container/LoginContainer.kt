package com.example.di.manual.container

import com.example.di.manual.beans.LoginUserData
import com.example.di.manual.repository.UserRepository
import com.example.di.manual.viewmodel.LoginViewModelFactory

/**
 * Create by SunnyDay 2022/06/30 21:23:15
 */

class LoginContainer(val userRepository: UserRepository) {

    val loginData = LoginUserData()

    val loginViewModelFactory = LoginViewModelFactory(userRepository)
}
