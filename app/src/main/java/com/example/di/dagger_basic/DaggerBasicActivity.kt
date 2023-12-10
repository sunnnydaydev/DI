package com.example.di.dagger_basic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.di.R
import com.example.di.dagger_basic.repository.Fragment
import com.example.di.manual.application.MyApplication
import javax.inject.Inject
import kotlin.reflect.KClass

class DaggerBasicActivity : AppCompatActivity() {

    @Inject
    lateinit var map: Map<KClass<out Fragment>, Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val daggerContainer = (application as MyApplication).getDaggerContainer()
        daggerContainer.injectMap(this)
        setContentView(R.layout.activity_dagger_basic)
        map.forEach { (t, u) ->
            println("key:${t.qualifiedName} value:$u")
        }
    }
}
