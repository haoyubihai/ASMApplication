package com.live.libasm.factories

import com.live.libasm.ITransform
import com.live.libasm.util.LogUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter


abstract class AbsTransFormClassFactory: ITransform {
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