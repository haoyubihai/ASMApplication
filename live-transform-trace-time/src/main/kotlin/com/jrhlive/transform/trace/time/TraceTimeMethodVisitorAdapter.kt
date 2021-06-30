package com.jrhlive.transform.trace.time

import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import java.util.regex.Matcher
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
class TraceTimeMethodVisitorAdapter(
    private val methodVisitor: MethodVisitor?,
    private val accessMethod: Int,
    var nameMethod: String? = null,
    private val desc: String?,
    val clazzName: String,
    val fullClassName: String,
    val simpleClassName: String
) :
    AdviceAdapter(Opcodes.ASM8, methodVisitor, accessMethod, nameMethod, desc) {

    //启动时间标记
    private var startTimeAddress: Int = -1
    private var parameterTypeList = mutableListOf<String>()
    private var parameterTypeCount = 0
    private var methodId = -1
    private var parameterIdentifier = 0 //入参内容标记

    // 当前局部变量值
    private var currentLocal = -1
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
        //return type => (I)V ->V
        methodId = Monitor.generateMethodId(
            MethodDesc(
                fullClassName,
                simpleClassName,
                nameMethod ?: "",
                desc,
                parameterTypeList,
                desc.substring(desc.lastIndexOf(')') + 1)
            )
        )
    }

    private val from = Label()
    private val to = Label()
    private val target = Label()


    override fun onMethodEnter() {
        insertStartTime()
        probeMethodParameter()
        visitLabel(from)
        visitTryCatchBlock(from, to, target, "java/lang/Exception")
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

        mv.visitLdcInsn(label)
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false)
        when (valueType) {
            "Z" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false)

            }
            "C" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false)

            }
            "B" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(B)Ljava/lang/StringBuilder;", false)

            }
            "S" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(S)Ljava/lang/StringBuilder;", false)

            }
            "I" -> {
                mv.visitVarInsn(Opcodes.ILOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false)

            }
            "F" -> {
                mv.visitVarInsn(Opcodes.FLOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(F)Ljava/lang/StringBuilder;", false)
            }
            "J" -> {
                mv.visitVarInsn(Opcodes.LLOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false)

            }
            "D" -> {
                mv.visitVarInsn(Opcodes.DLOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false)

            }
            else -> {
                mv.visitVarInsn(Opcodes.ALOAD, localCount)
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);

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

        // Object[] a = new Object[parameterTypeCount]
        // 先将数组的常量压入栈中
        when (parameterTypeCount) {
            0 -> {
                mv.visitInsn(Opcodes.ICONST_0)
            }
            1 -> {
                mv.visitInsn(Opcodes.ICONST_1)
            }
            2 -> {
                mv.visitInsn(Opcodes.ICONST_2)
            }
            3 -> {
                mv.visitInsn(Opcodes.ICONST_3)
            }
            4 -> {
                mv.visitInsn(Opcodes.ICONST_4)
            }
            5 -> {
                mv.visitInsn(Opcodes.ICONST_5)
            }
            else -> {
                mv.visitIntInsn(Opcodes.BIPUSH, parameterTypeCount)
            }
        }
        mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getDescriptor(Any::class.java))
        println("Type.getDescriptor(Any::class.java)===${Type.getDescriptor(Any::class.java)}")
        println("Type.getInternalName(Integer::class.java)===${Type.getInternalName(Integer::class.java)}")


        // 局部变量
        var localCount = if (isStaticMethod) -1 else 0

        for (index in 0 until parameterTypeCount) {
            mv.visitInsn(Opcodes.DUP)
            when (index) {
                0 -> {
                    mv.visitInsn(Opcodes.ICONST_0)
                }
                1 -> {
                    mv.visitInsn(Opcodes.ICONST_1)
                }
                2 -> {
                    mv.visitInsn(Opcodes.ICONST_2)
                }
                3 -> {
                    mv.visitInsn(Opcodes.ICONST_3)
                }
                4 -> {
                    mv.visitInsn(Opcodes.ICONST_4)
                }
                5 -> {
                    mv.visitInsn(Opcodes.ICONST_5)
                }
                else -> {
                    mv.visitVarInsn(Opcodes.BIPUSH, index)
                }
            }
            val type = parameterTypeList[index]
            println("parameter-type=${type}")
            when (type) {
                "Z" -> {
                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Boolean::class.java),
                        "valueOf",
                        "(Z)Ljava/lang/Boolean;",
                        false
                    )
                }
                "C" -> {
                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Character::class.java),
                        "valueOf",
                        "(C)Ljava/lang/Character;",
                        false
                    )
                }
                "B" -> {
                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Byte::class.java),
                        "valueOf",
                        "(B)Ljava/lang/Byte;",
                        false
                    )
                }
                "S" -> {
                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Short::class.java),
                        "valueOf",
                        "(S)Ljava/lang/Short;",
                        false
                    )
                }
                "I" -> {
                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Integer::class.java),
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                    )
                }
                "F" -> {
                    mv.visitVarInsn(Opcodes.FLOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Float::class.java),
                        "valueOf",
                        "(F)Ljava/lang/Float;",
                        false
                    )
                }
                "J" -> {
                    mv.visitVarInsn(Opcodes.LLOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Long::class.java),
                        "valueOf",
                        "(L)Ljava/lang/Long;",
                        false
                    )
                    localCount++
                }
                "D" -> {
                    mv.visitVarInsn(Opcodes.DLOAD, ++localCount)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Double::class.java),
                        "valueOf",
                        "(D)Ljava/lang/Double;",
                        false
                    )
                    localCount++
                }
                else -> {
                    mv.visitVarInsn(Opcodes.ALOAD, ++localCount)
                }
            }

            println("parameterTypeListSize=${parameterTypeList.size}----localCount=$localCount--type=$type")
            mv.visitInsn(Opcodes.AASTORE)
        }

        parameterIdentifier = newLocal(Type.LONG_TYPE)
        mv.visitVarInsn(Opcodes.ASTORE, parameterIdentifier)
//        createStringBuidler("---", "type", 3)


    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index)

        val methodParameterIndex = if (isStaticMethod) index else index - 1
        if (0 <= methodParameterIndex && methodParameterIndex < parameterTypeList.size) {
            Monitor.setMethodParameterGroup(methodId, name)
            println("visitLocalVariable---methodId=$methodId----name=$name---methodParameterIndex=$methodParameterIndex")
        }
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        //标志：try块结束
        mv.visitLabel(to)
        //标志：catch块开始位置
        mv.visitLabel(target)



        // 设置visitFrame：mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{"java/lang/String", Opcodes.INTEGER, Opcodes.LONG, "[Ljava/lang/Object;"}, 1, new Object[]{"java/lang/Exception"});
        val nLocal = (if (isStaticMethod) 0 else 1) + parameterTypeCount + if (parameterTypeCount == 0) 1 else 2
        val localObjs = arrayOfNulls<Any>(nLocal)
        var objIdx = 0
        if (!isStaticMethod) {
            localObjs[objIdx++] = className
        }
        for (parameter in parameterTypeList) {
            when (parameter) {
                "Z" -> {
                    localObjs[objIdx++] = Opcodes.INTEGER
                }
                "C" -> {
                    localObjs[objIdx++] = Opcodes.INTEGER
                }
                "B" -> {
                    localObjs[objIdx++] = Opcodes.INTEGER
                }
                "S" -> {
                    localObjs[objIdx++] = Opcodes.INTEGER
                }
                "I" -> {
                    localObjs[objIdx++] = Opcodes.INTEGER
                }
                "F" -> {
                    localObjs[objIdx++] = Opcodes.FLOAD
                }
                "J" -> {
                    localObjs[objIdx++] = Opcodes.LONG
                }
                "D" -> {
                    localObjs[objIdx++] = Opcodes.DOUBLE
                }
                else -> {
                    localObjs[objIdx++] = parameter
                }
            }
        }
        localObjs[objIdx++] = Opcodes.LONG
        if (parameterTypeCount > 0) {
            localObjs[objIdx] = "[Ljava/lang/Object;"
        }
        mv.visitFrame(
            Opcodes.F_FULL,
            nLocal,
            localObjs,
            1,
            arrayOf<Any>("java/lang/Exception")
        )

        // 异常信息保存到局部变量

        // 异常信息保存到局部变量
        val local = newLocal(Type.LONG_TYPE)
        println("xxxx:$local")
        mv.visitVarInsn(Opcodes.ASTORE, local)

        // 输出参数

        // 输出参数
        mv.visitVarInsn(Opcodes.LLOAD, startTimeAddress)
        mv.visitLdcInsn(methodId)
        if (parameterTypeList.isEmpty()) {
            mv.visitInsn(Opcodes.ACONST_NULL)
        } else {
            mv.visitVarInsn(Opcodes.ALOAD, parameterIdentifier)
        }
        mv.visitVarInsn(Opcodes.ALOAD, local)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC, Type.getInternalName(
                Monitor::class.java
            ), "point", "(JI[Ljava/lang/Object;Ljava/lang/Throwable;)V", false
        )

        // 抛出异常
        mv.visitVarInsn(Opcodes.ALOAD, local)
        mv.visitInsn(Opcodes.ATHROW)


        super.visitMaxs(maxStack, maxLocals)


    }

    private fun insertStartTime() {
        /**
        INVOKESTATIC java/lang/System.nanoTime ()J
        LSTORE 1
        long startTime = System.nanoTime();
         */
        methodVisitor?.run {
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false)
            currentLocal = newLocal(Type.LONG_TYPE)
            //取值方便
            startTimeAddress = currentLocal
            visitVarInsn(Opcodes.LSTORE, currentLocal)
        }
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

        if ((Opcodes.IRETURN<=opcode&&opcode<= Opcodes.RETURN)||opcode== Opcodes.ATHROW){
            probeMethodReturn(opcode)
            mv.visitVarInsn(Opcodes.LLOAD,startTimeAddress)
            println("startTimeAddress=${startTimeAddress}")
            mv.visitLdcInsn(methodId)
            if (parameterTypeList.isEmpty()){
                mv.visitInsn(Opcodes.ACONST_NULL)
            }else{
                mv.visitVarInsn(Opcodes.ALOAD,parameterIdentifier)
                println("parameterIdentifier=${parameterIdentifier}")
            }

            when(opcode){
                Opcodes.RETURN->{
                    mv.visitInsn(Opcodes.ACONST_NULL)
                }
                Opcodes.IRETURN->{

                    mv.visitVarInsn(Opcodes.ILOAD, currentLocal)
                    mv.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        Type.getInternalName(Int::class.java),
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                    )
                }
                else->{
                    mv.visitVarInsn(Opcodes.ALOAD, currentLocal)
                }
            }

            println("currentLocal：$currentLocal")
            //test
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC, Type.getInternalName(
                    Monitor::class.java
                ), "point", "(JI[Ljava/lang/Object;Ljava/lang/Object;)V", false
            )
        }

    }


    private fun probeMethodReturn(opcode: Int) {
        currentLocal = this.nextLocal
        when (opcode) {
            Opcodes.RETURN -> {
            }
            Opcodes.ARETURN -> {
                mv.visitVarInsn(Opcodes.ASTORE, currentLocal) // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(Opcodes.ALOAD, currentLocal) // 从局部变量indexbyte中装载引用类型值入栈。

            }
            Opcodes.IRETURN -> {
                mv.visitVarInsn(Opcodes.ISTORE, currentLocal) // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(Opcodes.ILOAD, currentLocal) // 从局部变量indexbyte中装载引用类型值入栈。

            }
            Opcodes.LRETURN -> {
                mv.visitVarInsn(Opcodes.LSTORE, currentLocal) // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(Opcodes.LLOAD, currentLocal) // 从局部变量indexbyte中装载引用类型值入栈。

            }
            Opcodes.DRETURN -> {
                mv.visitVarInsn(Opcodes.DSTORE, currentLocal) // 将栈顶引用类型值保存到局部变量indexbyte中。
                mv.visitVarInsn(Opcodes.DLOAD, currentLocal) // 从局部变量indexbyte中装载引用类型值入栈。

            }
        }
    }

    override fun catchException(start: Label?, end: Label?, exception: Type?) {
        super.catchException(start, end, exception)
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        println("annotation----222---methodVisitor--descriptor=$descriptor--visible=$visible")
        return super.visitAnnotation(descriptor, visible)
    }


}