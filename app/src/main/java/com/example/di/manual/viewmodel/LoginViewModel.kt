package com.example.di.manual.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.di.manual.repository.UserRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Create by SunnyDay 2022/06/27 16:12:22
 */
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login() {
        //仓库依赖->数据源->数据源中就是具体的那数据动作
        userRepository.remoteDataSource.loginService.getDataFromBaiDu().enqueue(object :
            Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("LoginViewModel","login#onFailure:${t.message}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("LoginViewModel","login#onResponse:数据请求成功！")
            }
        })
    }
}