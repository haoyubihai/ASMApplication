package com.jrhlive.transform.trace.time

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.ASM5
import org.objectweb.asm.Opcodes.ASM8
import java.util.logging.Logger

/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/4     11:59 AM
 * 用途:
 ***************************************
 */

class TraceTimeClassVisitor(private val classVisitor:ClassVisitor?) :ClassVisitor(ASM8,classVisitor),
    Opcodes {
    private var mClassName: String? = null
    /**
     * Visits a method of the class. This method *must* return a new [MethodVisitor]
     * instance (or null) each time it is called, i.e., it should not return a previously
     * returned visitor.
     *
     * @param access the method's access flags (see [Opcodes]). This parameter also indicates if
     * the method is synthetic and/or deprecated.
     * @param name the method's name.
     * @param descriptor the method's descriptor (see [Type]).
     * @param signature the method's signature. May be null if the method parameters,
     * return type and exceptions do not use generic types.
     * @param exceptions the internal names of the method's exception classes (see [     ][Type.getInternalName]). May be null.
     * @return an object to visit the byte code of the method, or null if this class
     * visitor is not interested in visiting the code of this method.
     */
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): org.objectweb.asm.MethodVisitor ?{
        var methodVisitor: MethodVisitor? = super.visitMethod(access, name, descriptor, signature,exceptions)
        //Base类中有两个方法：无参构造以及process方法，这里不增强构造方法
        if (name!="<init>"&& name?.startsWith("_$") != true && methodVisitor != null) {
//            methodVisitor = TraceTimeMethodVisitor(methodVisitor)
            methodVisitor = TraceTimeMethodVisitorAdapter(methodVisitor,access,name,descriptor)
        }
        return methodVisitor
    }

    /**
     * Visits the header of the class.
     *
     * @param version the class version. The minor version is stored in the 16 most significant bits,
     * and the major version in the 16 least significant bits.
     * @param access the class's access flags (see [Opcodes]). This parameter also indicates if
     * the class is deprecated [Opcodes.ACC_DEPRECATED] or a record [     ][Opcodes.ACC_RECORD].
     * @param name the internal name of the class (see [Type.getInternalName]).
     * @param signature the signature of this class. May be null if the class is not a
     * generic one, and does not extend or implement generic classes or interfaces.
     * @param superName the internal of name of the super class (see [Type.getInternalName]).
     * For interfaces, the super class is [Object]. May be null, but only for the
     * [Object] class.
     * @param interfaces the internal names of the class's interfaces (see [     ][Type.getInternalName]). May be null.
     */
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        mClassName = name

    }
}
