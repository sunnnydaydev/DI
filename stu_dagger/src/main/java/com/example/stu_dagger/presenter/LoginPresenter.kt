package com.example.stu_dagger.presenter

import android.util.Log
import com.example.stu_dagger.service.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Create by SunnyDay /07/11 20:35:29
 */
class LoginPresenter @Inject constructor(private val service:ApiService){
      fun login(){
        service.getLoginDataFromSever().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("LoginPresenter", "onResponse")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.i("LoginPresenter", "onFailure")
            }
        })
      }
}