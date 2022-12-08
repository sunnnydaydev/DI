package com.example.di.dagger_basic.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import javax.inject.Inject

/**
 * Create by SunnyDay 2022/07/01 14:06:42
 */
interface LoginRetrofitService  {
    @GET("/")
    fun login(): Call<ResponseBody>
}