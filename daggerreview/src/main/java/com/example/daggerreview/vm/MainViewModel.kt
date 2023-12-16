package com.example.daggerreview.vm

import android.util.Log
import com.example.daggerreview.net.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.concurrent.thread

/**
 * Create by SunnyDay /12/14 22:13:28
 * mock the android viewModel for test notice it is not a real viewModel
 */
class MainViewModel @Inject constructor(private val apiService: ApiService) {
    fun getSeverData() {
        apiService.getData().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("My test", "onResponse：${response.isSuccessful}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("My test", "onFailure：response is not Successful ")
            }

        })
    }

}