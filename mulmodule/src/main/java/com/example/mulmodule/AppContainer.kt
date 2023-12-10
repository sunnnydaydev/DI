package com.example.mulmodule

import dagger.Component

/**
 * Create by SunnyDay /12/10 13:58:34
 */

@Component
interface AppContainer{
    fun injectMainActivity(mainActivity: MainActivity)
}