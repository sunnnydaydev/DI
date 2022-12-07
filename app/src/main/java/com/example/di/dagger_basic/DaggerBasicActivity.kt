package com.example.di.dagger_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.di.R
import com.example.di.dagger_basic.vm.MainViewModel
import com.example.di.manual.application.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DaggerBasicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        val repo = daggerContainer.getUserRepository()
        val car = daggerContainer.getCar()

        lifecycleScope.launch (Dispatchers.IO){
            delay(5000)
            println("test-localDataSource1:${repo.localDataSource}")
            println("test-localDataSource2:${repo.localDataSource}")
            println("test-seat1:${car.seatProvider.get()}")
            println("test-seat2:${car.seatProvider.get()}")
        }
    }
}