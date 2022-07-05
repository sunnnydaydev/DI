package com.example.auto_di.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.auto_di.R
import com.example.auto_di.application.LoginComponent
import com.example.auto_di.application.MyApplication
import com.example.auto_di.viewmodel.LoginViewModel
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    // 生命周期跟随activity
    lateinit var loginComponent: LoginComponent
    @Inject
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        loginComponent = (application as MyApplication).component.loginComponent().create()
        loginComponent.inject(this@LoginActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel.login()

        val loginComponent2 = (application as MyApplication).component.loginComponent().create()
        Log.i("tag","loginComponent：$loginComponent")
        Log.i("tag","loginComponent2：$loginComponent2")
    }
}