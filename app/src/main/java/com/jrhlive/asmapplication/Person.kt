package com.jrhlive.asmapplication

/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/5/28     5:20 PM
 * 用途:
 ***************************************
 */
open class Person {

    fun sum() = 5/0.1

    constructor(){
        println("person----constructor")
    }

    init {
        println("person---init")
    }
}

class Son:Person{

    constructor(){
        println("son----constructor")
    }

    init {
        println("son--init")
    }

    fun pers(age:Int,name:String){

        val a = 1/0
        print(a)
    }
}