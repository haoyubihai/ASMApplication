package com.jrhlive.libasm.factories

import com.jrhlive.libasm.ITransform
import com.jrhlive.libasm.interceptor.Intercept
import com.jrhlive.libasm.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter


abstract class AbsTransFormClassFactory(val intercept: Intercept?): ITransform {
    override fun modifyClass(sourceBytes: ByteArray): ByteArray? {
        try {
            val classReader = ClassReader(sourceBytes)
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
            docClassReaderAccept(sourceBytes,classReader, classWriter)
            return classWriter.toByteArray()
        } catch (exception: Exception) {
            LogUtil.info("modify class exception = ${exception.printStackTrace()}")
        }
        return null
    }

    abstract fun docClassReaderAccept(sourceBytes: ByteArray,classReader:ClassReader,classWriter: ClassWriter)
}