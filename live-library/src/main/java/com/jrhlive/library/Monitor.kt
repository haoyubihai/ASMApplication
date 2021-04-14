package com.jrhlive.library

import com.google.gson.Gson
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReferenceArray


object Monitor {
    const val MAX_NUM = 1024 * 32
    val index = AtomicInteger(0)
    val methodTagArr = AtomicReferenceArray<MethodDesc>(MAX_NUM)
    private var methodParameterGroup = ConcurrentHashMap<Int, List<String>>()
    private var paramsList = mutableListOf<String>()

    fun generateMethodId(methodDesc: MethodDesc): Int {
        val methodId = index.getAndIncrement()
        if (methodId > MAX_NUM) return -1
        methodTagArr.set(methodId, methodDesc)
        return methodId
    }

    @JvmStatic
    fun pointNames(methodId: Int){
        methodParameterGroup[methodId]?.withIndex()?.onEach {
            println("$methodId-${it.index}=${it.value}")
        }

    }



    fun getMethodName(methodId: Int,index:Int):String?{
        return methodParameterGroup[methodId]?.getOrNull(index)
    }


    @Synchronized
    fun setMethodParameterGroup(methodId: Int, parameterName: String?) {
        parameterName?.let {
            paramsList.add(parameterName)
            methodParameterGroup.putIfAbsent(methodId, paramsList)
        }
    }

    fun point(
        methodId: Int,
        startNanos: Long,
        parameterValues: Array<Any>?,
        returnValues: Any?
    ) {
        methodTagArr.get(methodId)?.run {

            println("监控-Begin")
            println("方法-className=${fullClassName}.${methodName}")
            println(
                "入参:${Gson().toJson(parameterValues)}  入参[类型]:${Gson().toJson(parameterTypeList)}  入参[值]: ${Gson().toJson(
                    parameterValues
                )}"
            )
//            println("出参：${returnType}  出参[值]:${Gson().toJson(returnValues)}")
            println("耗时：${(System.nanoTime() - startNanos) / 1000_000}s")
            println("监控-End\r\n")

        }
    }

    fun point(methodId: Int, throwable: Throwable) {
        methodTagArr.get(methodId)?.run {
            println("监控 - Begin")
            System.out.println("方法：$fullClassName.$methodName")
            println("异常：" + throwable.message)
            println("监控 - End\r\n")
        }
    }

    fun point(
        startNanos: Long,
        methodId: Int,
        requests: Array<Any?>?,
        throwable: Throwable
    ) {
        val method: MethodDesc = methodTagArr.get(methodId)
        val parameters: List<String>? = methodParameterGroup.get(methodId)
        println("监控 - Begin")
        System.out.println("方法：" + method.fullClassName + "." + method.methodName)
        System.out.println(
            "入参：" + Gson().toJson(parameters).toString() + " 入参类型：" + Gson().toJson(method.parameterTypeList).toString() + " 入数[值]：" + Gson().toJson(requests)
        )
        println("异常：" + throwable.message)
        println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)")
        println("监控 - End\r\n")
    }

    fun point(
        startNanos: Long,
        methodId: Int,
        requests: Array<Any>?,
        response: Any?
    ) {
        val method: MethodDesc = methodTagArr?.get(methodId)
        val parameters: List<String>?= methodParameterGroup?.get(methodId)
        println("监控 - Begin")
        System.out.println("方法：" + method.fullClassName+ "." + method.methodName)
        System.out.println(
            "入参：" + Gson().toJson(parameters).toString() + " 入参类型：" + Gson().toJson(method.parameterTypeList).toString() + " 入数[值]：" + Gson().toJson(requests)
        )
        System.out.println(
            "出参：" + method.returnType + " 出参[值]：" + Gson().toJson(
                response
            )
        )
        println("耗时：" + (System.nanoTime() - startNanos) / 1000000 + "(s)")
        println("监控 - End\r\n")
    }

}