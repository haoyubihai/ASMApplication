package com.jrhlive.library

/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/8     5:22 PM
 * 用途:
 ***************************************
 */
object TimeCost {

    fun addStartTime(name: String, startTime: Long) {
        startTimeMap[name] = startTime
    }

    fun addEndTime(name: String, endTime: Long) {
        endTimeMap[name] = endTime
    }

    fun calTime(name: String){
        val s = startTimeMap[name]
        val e = endTimeMap[name]
        var timeStr = "name:$name"
        if(s!=null&&e!=null){
            timeStr= "start===$s------end====$e---------name:$name-执行耗时:${(e - s)/1000/1000}ms"
        }
        println(timeStr)
    }

    val startTimeMap = mutableMapOf<String, Long>()
    val endTimeMap = mutableMapOf<String, Long>()
}