package com.example.di.dagger_basic.anno

import com.example.di.dagger_basic.repository.Fragment
import dagger.MapKey
import java.lang.annotation.Documented
import javax.xml.transform.OutputKeys.METHOD
import kotlin.reflect.KClass

/**
 * Create by SunnyDay /12/13 21:22:15
 */
@Documented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FragmentKey(val value: KClass<out Fragment>)
