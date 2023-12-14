package com.example.daggerreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.daggerreview.vm.MainViewModel
import javax.inject.Inject
import javax.security.auth.login.LoginException

class MainActivity : AppCompatActivity() {
    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }

    @Inject
    lateinit var vm: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        container.injectMainActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("My test", "viewModel:${vm}")
        //viewModel:com.example.daggerreview.vm.MainViewModel@7b12f1f
    }

    fun test1() {
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

    fun test2() {
        Log.d("My test", "viewModel:${vm}")
    }
}