package com.example.auto_di.net.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay /07/03 20:26:17
 *
 * LoginRetrofitService 为Retrofit的service实例，这里不能使用@Inject构造的方案来创建实例。应该使用模块+@Provides方式。
 */
interface LoginRetrofitService {
    @GET("/")
    fun getLoginDataFromSever():Call<ResponseBody>
}