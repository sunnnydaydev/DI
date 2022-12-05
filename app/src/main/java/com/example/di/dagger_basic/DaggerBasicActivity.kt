package com.example.di.dagger_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.di.R
import com.example.di.dagger_basic.container.DaggerApplicationComponent
import com.example.di.manual.application.MyApplication

class DaggerBasicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        println("daggerContainer:$daggerContainer")

    }
}