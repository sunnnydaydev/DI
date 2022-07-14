package com.example.stu_dagger.presenter

import android.app.Dialog
import android.util.Log
import com.example.stu_dagger.beans.User
import com.example.stu_dagger.service.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Create by SunnyDay /07/13 21:11:45
 */
class HomePresenterImpl @Inject constructor(private val userService: ApiService) :HomePresenter {
    override fun logOut() {
           userService.logOutFromSever().enqueue(object : Callback<ResponseBody> {
               override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                   Log.i("HomePresenterImpl", "onResponse")
               }

               override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                   Log.i("HomePresenterImpl", "onFailure")
               }
           })
    }
}