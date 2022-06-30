package com.example.di.manual

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.di.R
import com.example.di.manual.application.MyApplication
import com.example.di.manual.repository.UserLocalDataSource
import com.example.di.manual.repository.UserRemoteDataSource
import com.example.di.manual.repository.UserRepository
import com.example.di.manual.service.LoginService
import com.example.di.manual.viewmodel.LoginViewModel
import retrofit2.Retrofit

class ManualLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_login)
        doLogin2()
    }

    /**
     * mock login action
     * */
    private fun doLogin1() {
        //4、网络数据源
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.baidu.com")
            .build()
            .create(LoginService::class.java)
        //3、数据源仓库依赖数据源
        val remoteDataSource = UserRemoteDataSource(retrofit)
        val localDataSource = UserLocalDataSource()
        //2、ViewModel需要一个数据仓库，数据仓库有不同的数据源：
        //(1)本地数据源
        //(2)网络数据源
        val userRepository = UserRepository(localDataSource, remoteDataSource)
        //1、ViewModel中做具体逻辑处理
        val loginViewModel = LoginViewModel(userRepository)
        loginViewModel.login()
    }

    /**
     * 基于doLogin1方案进行优化:
     * 1、容器管理（不是单利模式，而达到单例效果）
     * 2、工厂模式（viewModel的创建）
     * */
    private fun doLogin2() {
        // 使用容器的方式userRepository只初始化一次，类似单例而不是单例模式。
        val myApplication = application as MyApplication
        val loginViewModel = myApplication.container.loginViewModelFactory.create()
        loginViewModel.login()
    }
}