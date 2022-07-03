package com.example.auto_di.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.auto_di.R
import com.example.auto_di.application.MyApplication
import com.example.auto_di.viewmodel.LoginViewModel
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    //标记字段
    @Inject
    lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        // 初始化注入方法，原理很简单，把activity实例给Dagger，dagger则可以动态为activity对象成员赋值。
        (application as MyApplication).component.inject(this@LoginActivity)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel.login()
    }
}