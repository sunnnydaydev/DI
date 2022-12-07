package com.example.di.dagger_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.di.R
import com.example.di.dagger_basic.vm.MainViewModel
import com.example.di.manual.application.MyApplication

class DaggerBasicActivity : AppCompatActivity() {
    private val mainViewModel :MainViewModel by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(mainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        println("daggerContainer:$daggerContainer")



    }
}