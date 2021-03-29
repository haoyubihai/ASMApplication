package com.jrhlive.asmapplication

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
    }

    @TestMethodAnnotation(desc = "doActionAnnotation")
    fun doAction(){
        Thread.sleep(Random.nextLong(3000L))
        doActionTips("hello ",30)
    }

    fun doActionTips( name:String ,age :Int){
        println("name=$name--age=$age")
    }

    fun doMerge(num:Int,name:String){
        println("num=$num------name=$name")
    }
}

