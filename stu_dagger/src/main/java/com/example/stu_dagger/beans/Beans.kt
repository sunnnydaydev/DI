package com.example.stu_dagger.beans

import javax.inject.Inject

/**
 * Create by SunnyDay /07/12 20:52:35
 */
class User @Inject constructor() {
    var name = ""
    var age = 18

    override fun toString(): String {
        return "[name：$name ,age:$age]"
    }
}