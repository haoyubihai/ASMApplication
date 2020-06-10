package com.jrhlive.asmapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var line:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doAction()
        Thread.sleep(Random.nextLong(2000))
    }

    fun doAction(){
        Thread.sleep(Random.nextLong(3000L))
    }
}
