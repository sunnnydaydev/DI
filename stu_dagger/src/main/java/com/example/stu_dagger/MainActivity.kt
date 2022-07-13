package com.example.stu_dagger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stu_dagger.presenter.HomePresenter
import com.example.stu_dagger.presenter.LoginPresenter
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    companion object{
        const val tag = "MainActivity"
    }
    @Inject
    lateinit var loginPresent:LoginPresenter

    //HomePresenterImpl cannot be provided without an @Inject constructor or an @Provides-annotated method.
    @Inject
    lateinit var homePresenter:HomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).appComponent.inject(this)
        setContentView(R.layout.activity_main)
        loginPresent.login()

        homePresenter.logOut()
    }
}