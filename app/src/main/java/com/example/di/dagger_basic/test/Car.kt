package com.example.di.dagger_basic.test

import dagger.Lazy
import javax.inject.Inject

/**
 * Create by SunnyDay /12/07 20:54:07
 */
class Car @Inject constructor( val seatProvider: Lazy<Seat>)