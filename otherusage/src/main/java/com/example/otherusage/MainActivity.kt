package com.example.otherusage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = (application as MyApplication).getContainer()
        Log.d("My-test","application:${container.application()}")
        //application:com.example.otherusage.MyApplication@17f1035
    }
}