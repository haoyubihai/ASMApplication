package com.jrhlive.transform.trace.time

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter


/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/8     11:12 AM
 * 用途:
 * AdviceAdapter  see {@link https://asm.ow2.io/javadoc/org/objectweb/asm/commons/AdviceAdapter.html}
 ***************************************
 */

/**
 * 	AdviceAdapter(int api, MethodVisitor methodVisitor, int access, java.lang.String name, java.lang.String descriptor)
 */
class TraceTimeMethodVisitorAdapter(private val methodVisitor: MethodVisitor?, private val accessMethod:Int,  var nameMethod:String?=null, private val desc:String?):
    AdviceAdapter(Opcodes.ASM8,methodVisitor,accessMethod,nameMethod,desc) {

    override fun onMethodEnter() {


        methodVisitor?.run {
//            visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
//            visitLdcInsn("start------")
//            visitMethodInsn(org.objectweb.asm.Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)

            visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, "com/jrhlive/library/TimeCost", "INSTANCE", "Lcom/jrhlive/library/TimeCost;")
            visitLdcInsn(nameMethod)
            visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","nanoTime","()J",false)
            visitMethodInsn(Opcodes.INVOKEVIRTUAL,"com/jrhlive/library/TimeCost","addStartTime","(Ljava/lang/String;J)V",false)

        }
        super.onMethodEnter()
    }

    /**
     * GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    NEW java/lang/StringBuilder
    DUP
    INVOKESPECIAL java/lang/StringBuilder.<init> ()V
    LDC "\u82b1\u8d39\u65f6\u95f4="
    INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
    LLOAD 3
    LLOAD 1
    LSUB
    INVOKEVIRTUAL java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
    INVOKEVIRTUAL java/lang/StringBuilder.toString ()Ljava/lang/String;
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
     */
    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        methodVisitor?.run {
//            visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
//            visitLdcInsn("end---------")
//            visitMethodInsn(org.objectweb.asm.Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)

//            visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
//            visitVarInsn(LSTORE, 3)
//
//            visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//            visitTypeInsn(NEW, "java/lang/StringBuilder")
//            visitInsn(DUP)
//            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
//            visitLdcInsn("\u82b1\u8d39\u65f6\u95f4=");
//            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
//
//            visitVarInsn(LLOAD, 3);
//            visitVarInsn(LLOAD, 1);
//            visitInsn(LSUB);
//            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
//            visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
//            visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, "com/jrhlive/library/TimeCost", "INSTANCE", "Lcom/jrhlive/library/TimeCost;")
            visitLdcInsn(nameMethod)
            visitMethodInsn(Opcodes.INVOKESTATIC,"java/lang/System","nanoTime","()J",false)
            visitMethodInsn(Opcodes.INVOKEVIRTUAL,"com/jrhlive/library/TimeCost","addEndTime","(Ljava/lang/String;J)V",false)
            visitFieldInsn(org.objectweb.asm.Opcodes.GETSTATIC, "com/jrhlive/library/TimeCost", "INSTANCE", "Lcom/jrhlive/library/TimeCost;")
            visitLdcInsn(nameMethod)
            visitMethodInsn(Opcodes.INVOKEVIRTUAL,"com/jrhlive/library/TimeCost","calTime","(Ljava/lang/String;)V",false)
//            visitInsn(Opcodes.POP)


        }

    }

    override fun catchException(start: Label?, end: Label?, exception: Type?) {
        super.catchException(start, end, exception)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

}