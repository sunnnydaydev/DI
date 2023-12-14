package com.example.daggerreview

import com.example.daggerreview.entity.UserRepository
import dagger.Component
import javax.inject.Singleton

/**
 * Create by SunnyDay /12/14 21:28:46
 */

@Component
@Singleton
interface AppComponent{
  fun getUserRepository():UserRepository
}