package com.example.di.manual.viewmodel

/**
 * Create by SunnyDay 2022/06/27 17:58:04
 */
interface Factory<T> {
    fun create(): T
}