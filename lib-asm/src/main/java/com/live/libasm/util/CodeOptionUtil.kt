package com.live.libasm.util

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


object CodeOptionUtil {

    /**
     * 获取对应的返回值
     */
     fun getReturnCode(descriptor: String?,mv:MethodVisitor?): Int {
        return when (descriptor!!.subSequence(descriptor.indexOf(")") + 1, descriptor.length)) {
            "V" -> Opcodes.RETURN
            "I", "Z", "B", "C", "S" -> {
                mv?.visitInsn(Opcodes.ICONST_0)
                Opcodes.IRETURN
            }
            "D" -> {
                mv?.visitInsn(Opcodes.DCONST_0)
                Opcodes.DRETURN
            }
            "J" -> {
                mv?.visitInsn(Opcodes.LCONST_0)
                Opcodes.LRETURN
            }
            "F" -> {
                mv?.visitInsn(Opcodes.FCONST_0)
                Opcodes.FRETURN
            }
            else -> {
                mv?.visitInsn(Opcodes.ACONST_NULL)
                Opcodes.ARETURN
            }
        }
    }
}