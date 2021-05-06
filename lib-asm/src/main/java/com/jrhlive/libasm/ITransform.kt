package com.jrhlive.libasm

import com.jrhlive.libasm.interceptor.Intercept


interface ITransform {
    fun modifyClass(sourceBytes: ByteArray): ByteArray?

    /**
     * 对那些类需要处理
     */
    fun interceptor():Intercept?
}