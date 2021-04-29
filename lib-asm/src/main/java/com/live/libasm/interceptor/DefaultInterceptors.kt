package com.live.libasm.interceptor

object DefaultInterceptors {
    /**
     * 默认对以下文件不处理
     */
    val disablePackages = mutableListOf("android","Kotlin")
    val disableClassNames = mutableListOf("R","BuildConfig")
    val disableClassNamePrefixs = mutableListOf("R$")

    val ablePackages = mutableListOf<String>()
    val ableClassNames = mutableListOf<String>()
    val ableClassNamePrefixs = mutableListOf<String>()
}