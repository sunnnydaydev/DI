package com.example.auto_di.ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.auto_di.viewmodel.LoginViewModel
import javax.inject.Inject

/**
 * Create by SunnyDay /07/05 22:00:10
 */
class LoginPasswordFragment:Fragment() {
    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as LoginActivity).loginComponent.inject(this)
    }
}