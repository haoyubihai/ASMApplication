package com.jrhlive.transform.trace.time

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode
import java.util.regex.Pattern


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
class TraceTimeMethodVisitorAdapter2(
    private val paramNames: List<String>,
    private val classNode: ClassNode?,
    private val methodVisitor: MethodVisitor?,
    private val accessMethod: Int,
    var nameMethod: String? = null,
    private val desc: String?,
    private val signature: String?,
    val clazzName: String,
    val fullClassName: String,
    val simpleClassName: String
) :
    AdviceAdapter(Opcodes.ASM8, methodVisitor, accessMethod, nameMethod, desc) {

    //启动时间标记
    private var parameterTypeList = mutableListOf<String>()
    private var parameterTypeCount = 0
    private var isStaticMethod: Boolean = false
    private var className: String = ""


    init {
        className = clazzName
        // 判断是否为静态方法，非静态方法中局部变量第一个值是this，静态方法是第一个入参参数
        isStaticMethod = (accessMethod and Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
        //(String var1,Object var2,String var3,int var4,long var5,int[] var6,Object[][] var7,Req var8)=="(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;IJ[I[[Ljava/lang/Object;Lorg/itstack/test/Req;)V"
        val matcher = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})")
            .matcher(desc!!.substring(0, desc.lastIndexOf(')') + 1))
        while (matcher.find()) {
            parameterTypeList.add(matcher.group(1))
        }
        parameterTypeCount = parameterTypeList.size

    }



    private val from = Label()
    private val to = Label()
    private val target = Label()


    override fun onMethodEnter() {
        //增加try catch
        visitTryCatchBlock(from, to, target, "java/lang/Exception")
        visitLabel(from)
        probeMethodParameter()
        super.onMethodEnter()
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        //标志：try块结束
        mv.visitLabel(to)
//        mv.visitJumpInsn(Opcodes.GOTO, catchEnd)
        //标志：catch块开始位置
        mv.visitLabel(target)

//        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, arrayOf("java/lang/Exception"))
//
        val local = newLocal(Type.getType("Ljava/lang/Exception;"))
        mv.visitVarInsn(Opcodes.ASTORE, local)

////         抛出异常
        mv.visitVarInsn(Opcodes.ALOAD, local)

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception",
            "printStackTrace", "()V", false);
//        mv.visitInsn(Opcodes.ATHROW)
//        mv.visitLabel(catchEnd)
        //判断方法的返回类型
        mv.visitInsn(getReturnCode(descriptor = desc))
        super.visitMaxs(maxStack, maxLocals)

    }

    /**
     * 获取对应的返回值
     */
    private fun getReturnCode(descriptor: String?): Int {
        return when (descriptor!!.subSequence(descriptor.indexOf(")") + 1, descriptor.length)) {
            "V" -> Opcodes.RETURN
            "I", "Z", "B", "C", "S" -> {
                mv.visitInsn(Opcodes.ICONST_0)
                Opcodes.IRETURN
            }
            "D" -> {
                mv.visitInsn(Opcodes.DCONST_0)
                Opcodes.DRETURN
            }
            "J" -> {
                mv.visitInsn(Opcodes.LCONST_0)
                Opcodes.LRETURN
            }
            "F" -> {
                mv.visitInsn(Opcodes.FCONST_0)
                Opcodes.FRETURN
            }
            else -> {
                mv.visitInsn(Opcodes.ACONST_NULL)
                Opcodes.ARETURN
            }
        }
    }

    private fun appendParam(label: String, valueType: String, localCount: Int) {
        println("lable-$label---valueType=$valueType---localCount=$localCount")
        val variableName =paramNames.getOrNull(localCount-1)
        //append variableName:
        mv.visitLdcInsn("$variableName:")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        when (valueType) {
            "Z" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Z)Ljava/lang/StringBuilder;",
                    false
                )

            }
            "C" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(C)Ljava/lang/StringBuilder;",
                    false
                )

            }
            "B" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(B)Ljava/lang/StringBuilder;",
                    false
                )

            }
            "S" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(S)Ljava/lang/StringBuilder;",
                    false
                )

            }
            "I" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(I)Ljava/lang/StringBuilder;",
                    false
                )

            }
            "F" -> {
                mv.visitVarInsn(Opcodes.FLOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(F)Ljava/lang/StringBuilder;",
                    false
                )
            }
            "J" -> {
                mv.visitVarInsn(Opcodes.LLOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(J)Ljava/lang/StringBuilder;",
                    false
                )
            }
            "D" -> {
                mv.visitVarInsn(Opcodes.DLOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(D)Ljava/lang/StringBuilder;",
                    false
                )

            }
            else -> {
                mv.visitVarInsn(Opcodes.ALOAD, localCount)
                mv.visitMethodInsn(
                    INVOKEVIRTUAL,
                    "java/lang/StringBuilder",
                    "append",
                    "(Ljava/lang/Object;)Ljava/lang/StringBuilder;",
                    false
                );

            }
        }
        if (localCount!=parameterTypeCount){
            mv.visitLdcInsn(",")
            mv.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
                false
            )
        }
    }

    /**
     * 当 int 取值 -1~5 时，JVM 采用 iconst 指令将常量压入栈中。
     * int 取值 0~5 时 JVM 采用 iconst_0、iconst_1、iconst_2、iconst_3、iconst_4、iconst_5 指令将常量压入栈中，取值 -1 时采用 iconst_m1 指令将常量压入栈中。
     * 当 int 取值 -128~127 时，JVM 采用 bipush 指令将常量压入栈中。
     */
    private fun probeMethodParameter() {
        if (parameterTypeCount <= 0) return
        // 局部变量
        var localCount = if (isStaticMethod) -1 else 0
        //先创建一个StringBuilder
        methodVisitor?.run {
            visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        }

        //append fullClassName  methodName :{
        mv.visitLdcInsn("监控--class=${fullClassName}-----methodName=$nameMethod:{ ")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )

        //append
        for (index in 0 until parameterTypeCount) {
            val type = parameterTypeList[index]

            when (type) {
                "Z" -> {
                    appendParam("Z", "$type", ++localCount)
                }
                "C" -> {

                    appendParam("C", "$type", ++localCount)
                }
                "B" -> {

                    appendParam("B", "$type", ++localCount)
                }
                "S" -> {

                    appendParam("S", "$type", ++localCount)
                }
                "I" -> {

                    appendParam("I", "$type", ++localCount)
                }
                "F" -> {

                    appendParam("F", "$type", ++localCount)
                }
                "J" -> {

                    localCount++
                    appendParam("J", "$type", ++localCount)
                }
                "D" -> {

                    localCount++
                    appendParam("D", "$type", ++localCount)
                }
                else -> {
                    appendParam("O", "$type", ++localCount)
                }
            }
        }

        //append "}"
        mv.visitLdcInsn("} ")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )

        //toString
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)

    }
}