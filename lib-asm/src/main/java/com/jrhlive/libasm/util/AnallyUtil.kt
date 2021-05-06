package com.jrhlive.libasm.util

import java.io.File
import java.nio.charset.Charset
object AnallyUtil {

    private const val destFileSavePath  = "/Users/jiaruihua/Desktop/asmLog.txt"


    @JvmStatic
    fun appendFile(text:String, destFile:String= destFileSavePath){
        val f = File(destFile)
        if(!f.exists()){
            f.createNewFile()
        }
        f.appendText("\n$text\n", Charset.defaultCharset())
    }
    @JvmStatic
    fun reset(destFile:String= destFileSavePath){
       val f =  File(destFile)
        if (f.exists())f.delete()
    }

}