package com.example.otherusage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = (application as MyApplication).getContainer()
        Log.d("My-test","cat:${container.getCat()}")
        Log.d("My-test","Animal:${container.getAnimal()}")
        //D  cat:com.example.otherusage.entity.Cat@7b12f1f
        //D  Animal:com.example.otherusage.entity.Dog@3c6b26c
    }
}