package com.example.di.dagger_basic.test

import javax.inject.Inject

/**
 * Create by SunnyDay /12/07 20:54:29
 */
class Seat @Inject constructor(){
    init {
        println("test-Seat Init")
    }
}