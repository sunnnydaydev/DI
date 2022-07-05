package com.example.auto_di.viewmodel

import android.util.Log
import com.example.auto_di.anno.ActivityScope
import com.example.auto_di.repository.UserRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Create by SunnyDay /07/03 20:22:19
 * ViewModel类，由于模拟登录流程，这里暂且也当做普通的类处理。不继承ViewModel了。因为ViewModel的创建也不是普通new出来的。
 */
@ActivityScope
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) {

    fun login() {
        val service = userRepository.userRemoteDataSource.loginService
        val service2 = userRepository.userRemoteDataSource.loginService
        Log.i("LoginViewModel", "service实例1：$service")
        Log.i("LoginViewModel", "service实例2：$service2")
        service.getLoginDataFromSever().enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("LoginViewModel", "onResponse")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("LoginViewModel", "onFailure")
            }

        })
    }

}