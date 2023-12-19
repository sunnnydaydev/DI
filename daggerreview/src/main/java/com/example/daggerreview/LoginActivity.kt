package com.example.daggerreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Log.d("my-test","userInfo login activity:${loginComponent.getUserInfo()}")
        Log.d("my-test","api service login activity:${loginComponent.getApiService()}")
    }
}