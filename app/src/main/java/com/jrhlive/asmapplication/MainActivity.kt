package com.jrhlive.asmapplication

import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        doAction()
        Thread.sleep(Random.nextLong(2000))
        doMerge(3,"jack")

        val a =9/0

    }

    @TestMethodAnnotation(desc = "doActionAnnotation")
    fun doAction(){
        Thread.sleep(Random.nextLong(3000L))
        doActionTips("hello ",30)


    }

    fun doActionTips( name:String ,age :Int){

        val point:PointF?= null
        print(point!!.x)
    }

    fun doMerge(num:Int,name:String){
    }
}

