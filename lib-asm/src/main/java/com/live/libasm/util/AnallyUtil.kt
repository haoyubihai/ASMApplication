package com.live.libasm.util

import java.io.File
import java.nio.charset.Charset

object AnallyUtil {

    private const val destFileSavePath  = "/Users/jiaruihua/Desktop/asmLog.txt"


    fun appendFile(text:String, destFile:String= destFileSavePath){
        val f = File(destFile)
        if(!f.exists()){
            f.createNewFile()
        }
        f.appendText(text, Charset.defaultCharset())
    }

    fun reset(destFile:String= destFileSavePath){
       val f =  File(destFile)
        if (f.exists())f.delete()
    }

}