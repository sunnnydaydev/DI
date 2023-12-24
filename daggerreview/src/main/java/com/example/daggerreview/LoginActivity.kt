package com.example.daggerreview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.daggerreview.entity.LoginViewModel
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private val container: AppComponent by lazy { (application as MyApplication).getContainer() }
    private val loginComponent: LoginComponent by lazy {
        container.getLoginComponent().create()
    }

    @Inject
    lateinit var loginView1:LoginViewModel

    @Inject
    lateinit var loginView2:LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginComponent.inject(this)
        setContentView(R.layout.activity_login)

        Log.d("my-test","loginView1:${loginView1}")
        Log.d("my-test","loginView2:${loginView2}")

        //loginView1:com.example.daggerreview.entity.LoginViewModel@a45233a
        //loginView2:com.example.daggerreview.entity.LoginViewModel@a45233a

    }
}