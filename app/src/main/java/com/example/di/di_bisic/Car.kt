package com.example.di.di_bisic

import android.util.Log

/**
 * Create by SunnyDay 2022/06/27 21:01:57
 */
class Car {
    fun start(engine: Engine?) {
        engine?.openEngine()
    }
}

class Engine {
    fun openEngine() {
        Log.d("tag", "汽车已启动！")
    }
}

fun main() {
    val car = Car()
    car.start(Engine())
}

