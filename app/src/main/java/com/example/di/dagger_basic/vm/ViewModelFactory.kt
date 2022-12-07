package com.example.di.dagger_basic.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Create by SunnyDay /12/07 10:07:15
 */
class ViewModelFactory<T>@Inject constructor(
    private val modelProvider: Provider<T>)
    :ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = modelProvider.get() as T
}