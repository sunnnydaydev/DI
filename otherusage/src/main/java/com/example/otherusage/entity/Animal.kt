package com.example.otherusage.entity

import javax.inject.Inject

/**
 * Create by SunnyDay /12/24 19:21:10
 */
interface Animal

class Dog @Inject constructor() : Animal

class Cat : Animal