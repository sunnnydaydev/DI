package com.example.di.manual.viewmodel

import com.example.di.manual.repository.UserRepository

/**
 * Create by SunnyDay 2022/06/27 17:57:36
 * viewModel 工厂类
 */
class LoginViewModelFactory(private val userRepository: UserRepository):Factory<LoginViewModel> {
    override fun create(): LoginViewModel {
        return LoginViewModel(userRepository)
    }

}