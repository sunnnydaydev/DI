package com.example.stu_dagger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.stu_dagger.components.ApplicationComponent
import com.example.stu_dagger.components.DaggerApplicationComponent
import com.example.stu_dagger.presenter.LoginPresenter
import com.example.stu_dagger.repo.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注入
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        // 使用字段
        Log.d(tag,"loginPresent:$loginPresent")
    }
}