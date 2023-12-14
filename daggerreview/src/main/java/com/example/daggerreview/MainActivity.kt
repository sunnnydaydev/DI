package com.example.daggerreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import javax.security.auth.login.LoginException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = (application as MyApplication).getContainer()
        val repo1 = container.getUserRepository()
        val repo2 = container.getUserRepository()

        val userRemoteDataSource1 = repo1.userRemoteDataSource
        val userRemoteDataSource2 = repo1.userRemoteDataSource
        val userRemoteDataSource3 = repo2.userRemoteDataSource

        Log.d("My test", "userRemoteDataSource1:${userRemoteDataSource1}")
        Log.d("My test", "userRemoteDataSource2:${userRemoteDataSource2}")

        Log.d("My test", "userRemoteDataSource3:${userRemoteDataSource3}")
        //userRemoteDataSource1:com.example.daggerreview.entity.UserRemoteDataSource@7b12f1f
        //userRemoteDataSource2:com.example.daggerreview.entity.UserRemoteDataSource@7b12f1f
        //userRemoteDataSource3:com.example.daggerreview.entity.UserRemoteDataSource@3c6b26c
    }
}