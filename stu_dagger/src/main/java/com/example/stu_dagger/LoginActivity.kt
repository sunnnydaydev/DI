package com.example.stu_dagger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stu_dagger.presenter.LoginPresenter
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {
    companion object {
        const val tag = "MainActivity"
    }

    @Inject
    lateinit var loginPresent: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as MyApplication
        val loginComponent = application.appComponent.getLoginComponent().create()
        loginComponent.inject(this)
        setContentView(R.layout.activity_injecct_field)
        loginPresent.login()
    }
}