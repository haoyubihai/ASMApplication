package com.jrhlive.transform.trace.time

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Opcodes.*

/**
 ***************************************
 * 项目名称:ASMApplication
 * @Author jiaruihua
 * 邮箱：jiaruihua@ksjgs.com
 * 创建时间: 2020/6/4     5:39 PM
 * 用途:
 ***************************************
 */
class TraceTimeMethodVisitor(private val visitor: MethodVisitor?):MethodVisitor(ASM8,visitor),Opcodes {
    /** Starts the visit of the method's code, if any (i.e. non abstract method).  */
    override fun visitCode() {
        super.visitCode()
        visitor?.run {
            visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            visitLdcInsn("start----")
            visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
        }
    }

    /**
     * Visits a zero operand instruction.
     *
     * @param opcode the opcode of the instruction to be visited. This opcode is either NOP,
     * ACONST_NULL, ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
     * LCONST_0, LCONST_1, FCONST_0, FCONST_1, FCONST_2, DCONST_0, DCONST_1, IALOAD, LALOAD,
     * FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD, IASTORE, LASTORE, FASTORE, DASTORE,
     * AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2,
     * SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV,
     * FDIV, DDIV, IREM, LREM, FREM, DREM, INEG, LNEG, FNEG, DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR,
     * LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR, I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I,
     * D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG, IRETURN, LRETURN, FRETURN,
     * DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, or MONITOREXIT.
     */
    override fun visitInsn(opcode: Int) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
            || opcode == Opcodes.ATHROW) {
            //方法在返回之前，打印"end"
            visitor?.run {
                visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                visitLdcInsn("end----")
                visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
            }
        }
        visitor?.visitInsn(opcode);
    }
}