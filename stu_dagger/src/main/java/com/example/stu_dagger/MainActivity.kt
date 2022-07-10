package com.example.stu_dagger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.stu_dagger.components.ApplicationComponent
import com.example.stu_dagger.components.DaggerApplicationComponent
import com.example.stu_dagger.repo.*

class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 两种方式获取被生成类对象
        val userRemoteDataSource1:UserRemoteDataSource = UserRemoteDataSource_Factory.newInstance()
        val userRemoteDataSource2:UserRemoteDataSource  = UserRemoteDataSource_Factory.create().get()
        Log.d(tag,"userRemoteDataSource1:$userRemoteDataSource1")
        Log.d(tag,"userRemoteDataSource2:$userRemoteDataSource2")
        /**
           log:
           D/MainActivity: userRemoteDataSource1:com.example.stu_dagger.repo.UserRemoteDataSource@67f5a77
           D/MainActivity: userRemoteDataSource2:com.example.stu_dagger.repo.UserRemoteDataSource@d3f6e4
         */

        val userRepository1:UserRepository = UserRepository_Factory.newInstance(
            UserLocalDataSource(),
            UserRemoteDataSource()
        )
        val userRepository2: UserRepository = UserRepository_Factory.create(
            UserLocalDataSource_Factory.create(),
            UserRemoteDataSource_Factory.create()
        ).get()
        Log.d(tag,"userRepository1:$userRepository1")
        Log.d(tag,"userRepository2:$userRepository2")
        /**
         * D/MainActivity: userRepository1:com.example.stu_dagger.repo.UserRepository@d4b9d50
         * D/MainActivity: userRepository2:com.example.stu_dagger.repo.UserRepository@f008b49
         * */

        // DaggerApplicationComponent
        val container1 = DaggerApplicationComponent.create()
        val container2 = DaggerApplicationComponent.create()
        //加上@Singleton后验证下DaggerApplicationComponent是否单例
        Log.d(tag,"container1:$container1")
        Log.d(tag,"container2:$container2")

        /**
        contain对象不同：
        D/MainActivity: container1:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
        D/MainActivity: container2:com.example.stu_dagger.components.DaggerApplicationComponent@511216f
         */

        val userRepository3:UserRepository = container1.getUserRepository()
        val userRepository4:UserRepository = container1.getUserRepository()
        //加上@Singleton后验证下UserRepository获取是否单例。
        Log.d(tag,"userRepository3:$userRepository3")
        Log.d(tag,"userRepository4:$userRepository3")
        /**
        userRepository对象相同
        D/MainActivity: userRepository3:com.example.stu_dagger.repo.UserRepository@b553e7c
        D/MainActivity: userRepository3:com.example.stu_dagger.repo.UserRepository@b553e7c
         * */

        // DaggerApplicationComponent
        val container3 = (application as MyApplication).appComponent
        val container4 = (application as MyApplication).appComponent
        //加上@Singleton后验证下DaggerApplicationComponent是否单例
        Log.d(tag,"container3:$container3")
        Log.d(tag,"container4:$container4")
        /**
        D/MainActivity: container3:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
        D/MainActivity: container3:com.example.stu_dagger.components.DaggerApplicationComponent@121bc4e
         * */

    }
}