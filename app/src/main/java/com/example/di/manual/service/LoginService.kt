package com.example.di.manual.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay 2022/06/27 16:50:51
 */
interface LoginService {
    /**
     * get请求，请求百度网页的接口。
     * */
    @GET("/")
    fun getDataFromBaiDu(): Call<ResponseBody>
}