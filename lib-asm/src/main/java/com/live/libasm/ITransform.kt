package com.live.libasm


interface ITransform {
    fun modifyClass(sourceBytes: ByteArray): ByteArray?
}