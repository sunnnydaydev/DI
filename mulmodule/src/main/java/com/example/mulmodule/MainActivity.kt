package com.example.mulmodule

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginmodule.ui.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).getAppComponent().injectMainActivity(this)
        super.onCreate(savedInstanceState)
        startActivity(Intent(this,LoginActivity::class.java))
    }

}