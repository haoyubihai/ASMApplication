package com.jrhlive

object LibAsmClass {

    fun helloAsm(name:String){
        println("Lib----Ams---$name")
        testTryCatch(null)
    }

    fun testTryCatch(hello:String?){
        print(hello!!.length)
    }
}
