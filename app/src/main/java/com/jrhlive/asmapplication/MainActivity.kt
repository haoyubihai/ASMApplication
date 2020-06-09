package com.jrhlive.asmapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import com.jrhlive.library.TimeCost
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var line:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test(0)
        Thread.sleep(Random.nextLong(2000))

        System.nanoTime()
    }

    fun test(size:Int){
        Thread.sleep(Random.nextLong(3000L))
    }
}
