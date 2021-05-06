package com.jrhlive.libasm.visitors

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

abstract class AbsMethodVisitorAdvice(
    private val api:Int,
    private val methodVisitor: MethodVisitor?,
    private val  accessMethod:Int,
    private val  nameMethod:String,
    private val  desc:String
): AdviceAdapter(api, methodVisitor, accessMethod, nameMethod, desc) {
}