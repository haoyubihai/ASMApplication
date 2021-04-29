package com.live.libasm

import com.live.libasm.interceptor.Intercept


interface ITransform {
    fun modifyClass(sourceBytes: ByteArray): ByteArray?

    /**
     * 对那些类需要处理
     */
    fun interceptor():Intercept?
}