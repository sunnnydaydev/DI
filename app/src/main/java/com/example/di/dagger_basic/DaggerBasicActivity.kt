package com.example.di.dagger_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.di.R
import com.example.di.dagger_basic.container.DaggerApplicationComponent
import com.example.di.dagger_basic.service.LoginRetrofitService

class DaggerBasicActivity : AppCompatActivity() {
    companion object{
        const val  tag = "DaggerBasicActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)
        // 创建容器对象
        val daggerApplicationComponent = DaggerApplicationComponent.create()
        // 获取实例对象
        val repository = daggerApplicationComponent.repository()
        val repository1 = daggerApplicationComponent.repository()


        val daggerApplicationComponent1 = DaggerApplicationComponent.create()
        Log.d(tag,"daggerApplicationComponent1:$daggerApplicationComponent1")
        Log.d(tag,"daggerApplicationComponent:$daggerApplicationComponent")

        Log.d(tag,"repository:$repository")
        Log.d(tag,"repository1:$repository1")

    }
}