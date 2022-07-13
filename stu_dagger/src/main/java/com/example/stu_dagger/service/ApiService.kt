package com.example.stu_dagger.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay /07/12 20:25:18
 */
interface ApiService {
    @GET("/")
    fun getLoginDataFromSever(): Call<ResponseBody>

    @GET("/")
    fun logOutFromSever(): Call<ResponseBody>
}