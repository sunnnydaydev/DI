package com.example.daggerreview.net

import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by SunnyDay /12/16 18:04:58
 */
interface ApiService {
    @GET("/")
    fun getData():Call<ResponseBody>
}