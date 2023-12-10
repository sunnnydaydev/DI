package com.example.loginmodule.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatTextView
import com.example.loginmodule.R
import com.example.loginmodule.dagger.DaggerLoginContainer
import com.example.loginmodule.dagger.LoginContainer
import com.example.loginmodule.dagger.ProviderLoginContainer
import com.example.loginmodule.entity.User
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        val  loginContainer :LoginContainer = (application as ProviderLoginContainer).providerLoginContainer()
        loginContainer.injectLoginActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<AppCompatTextView>(R.id.tvText).text = user.javaClass.simpleName

    }
}