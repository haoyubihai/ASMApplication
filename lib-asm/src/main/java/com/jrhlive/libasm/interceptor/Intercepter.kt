package com.jrhlive.libasm.interceptor

/**
 *对配置的包名 ，类名，方法名 进行处理
 * 不同属性的配置用","分割
 * @property packagePrefix 包名 配置的越详细 控制的粒度就越细
 * @property classFullName 类名
 * @property methodNames  方法名
 */
open class Intercept(
    var packagePrefixs: String = "*",
    var simpleClassNames: String = "*",
    var simpleMethodNames: String = "*",
    /**
     * 0,dir,jar 都处理
     * 1,dir 处理
     * 2,jar 处理
     */
    var dealType:String ="all"
)

sealed class DealType(val value:String){
    object ALL:DealType("all")
    object DIR:DealType("dir")
    object JAR:DealType("jar")
}

open class InterceptorOptions(
    val packages: List<String>,
    val simpleClassNames: List<String>,
    val methodNames: List<String>
)