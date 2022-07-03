package com.example.di.dagger_basic.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay 2022/07/01 14:06:42
 *
 * 这个接口不能直接实例，需要使用retrofit 提供的Build方式创建。因此不能这里构造进行@ject了。参看
 * @link[com.example.dopractice.module.NetworkModule]
 */
interface LoginRetrofitService {
    @GET("/")
    fun getDataFromBaidu(): Call<ResponseBody>
}