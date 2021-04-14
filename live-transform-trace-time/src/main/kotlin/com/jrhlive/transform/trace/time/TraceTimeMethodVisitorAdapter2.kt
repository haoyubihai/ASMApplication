package com.jrhlive.transform.trace.time

import org.objectweb.asm.*
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
        println("life--&&&&&&&&&---onMethodEnter")
        probeMethodParameter()
        super.onMethodEnter()
    }


    private fun createStringBuidler(label: String, valueType: String, localCount: Int) {
        println("lable-$label---valueType=$valueType---localCount=$localCount")
        methodVisitor?.run {
            visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
            visitInsn(DUP)
            visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        }

        var variableName =paramNames.getOrNull(localCount-1)

        mv.visitLdcInsn("监控--class=${fullClassName}-----methodName=$nameMethod--$variableName==")
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
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)


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

        for (index in 0 until parameterTypeCount) {
            val type = parameterTypeList[index]
            println("parameter-type=${type}")
            when (type) {
                "Z" -> {
                    createStringBuidler("Z", "$type", ++localCount)
                }
                "C" -> {

                    createStringBuidler("C", "$type", ++localCount)
                }
                "B" -> {

                    createStringBuidler("B", "$type", ++localCount)
                }
                "S" -> {

                    createStringBuidler("S", "$type", ++localCount)
                }
                "I" -> {

                    createStringBuidler("I", "$type", ++localCount)
                }
                "F" -> {

                    createStringBuidler("F", "$type", ++localCount)
                }
                "J" -> {

                    localCount++
                    createStringBuidler("J", "$type", ++localCount)
                }
                "D" -> {

                    localCount++
                    createStringBuidler("D", "$type", ++localCount)
                }
                else -> {
                    createStringBuidler("O", "$type", ++localCount)
                }
            }
        }
    }
}