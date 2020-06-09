package com.jrhlive.transform.trace.time

/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/8     5:22 PM
 * 用途:
 ***************************************
 */
class TimeCost {

    fun addStartTime(name:String,startTime:Long){
        startTimeMap[name] = startTime
    }

    fun addEndTime(name: String,endTime:Long){
        endTimeMap[name]= endTime
    }

    fun calTime(name: String):String{
        val s = startTimeMap[name]
        val e = endTimeMap[name]
        s?.let {start->
            e?.let {end->
                return "name:$name-花费时间=${end-start}"
            }
        }
        return "name:$name"

    }

    companion object{
        val startTimeMap = mutableMapOf<String,Long>()
        val endTimeMap = mutableMapOf<String,Long>()
    }
}