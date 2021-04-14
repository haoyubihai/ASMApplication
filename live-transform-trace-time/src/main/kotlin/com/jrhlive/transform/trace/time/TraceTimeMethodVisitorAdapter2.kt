package com.jrhlive.transform.trace.time

import com.jrhlive.transform.trace.MethodParamNamesScaner
import org.objectweb.asm.*
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.io.FileInputStream
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
        paramNames?.onEach {
            println("name==$nameMethod----$it")
        }

        println("life--&&&&&&&&&---init-----className=$className-------fullClassName=$fullClassName---name=$nameMethod---desc=$desc--")

    }



    private val from = Label()
    private val to = Label()
    private val target = Label()


    override fun onMethodEnter() {
        println("life--&&&&&&&&&---onMethodEnter")
        insertStartTime()

//        visitLabel(from)
//        visitTryCatchBlock(from, to, target, "java/lang/Exception")
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

//        val variables  = ClassHelper.getMethodLocalVariables(classNode,name,methodDesc,signature)
//        ClassHelper.printVariables(variables)
//
//        var variableName=variables?.getOrNull(localCount)

        var variableName =paramNames.getOrNull(localCount-1)


        mv.visitLdcInsn("----$variableName==")
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


//
//        // Object[] a = new Object[parameterTypeCount]
//        // 先将数组的常量压入栈中
//        when (parameterTypeCount) {
//            0 -> {
//                mv.visitInsn(Opcodes.ICONST_0)
//            }
//            1 -> {
//                mv.visitInsn(Opcodes.ICONST_1)
//            }
//            2 -> {
//                mv.visitInsn(Opcodes.ICONST_2)
//            }
//            3 -> {
//                mv.visitInsn(Opcodes.ICONST_3)
//            }
//            4 -> {
//                mv.visitInsn(Opcodes.ICONST_4)
//            }
//            5 -> {
//                mv.visitInsn(Opcodes.ICONST_5)
//            }
//            else -> {
//                mv.visitIntInsn(Opcodes.BIPUSH, parameterTypeCount)
//            }
//        }
//        mv.visitTypeInsn(Opcodes.ANEWARRAY, Type.getDescriptor(Any::class.java))
//        println("Type.getDescriptor(Any::class.java)===${Type.getDescriptor(Any::class.java)}")
//        println("Type.getInternalName(Integer::class.java)===${Type.getInternalName(Integer::class.java)}")


        // 局部变量
        var localCount = if (isStaticMethod) -1 else 0

        for (index in 0 until parameterTypeCount) {
//            mv.visitInsn(Opcodes.DUP)
//            when (index) {
//                0 -> {
//                    mv.visitInsn(Opcodes.ICONST_0)
//                }
//                1 -> {
//                    mv.visitInsn(Opcodes.ICONST_1)
//                }
//                2 -> {
//                    mv.visitInsn(Opcodes.ICONST_2)
//                }
//                3 -> {
//                    mv.visitInsn(Opcodes.ICONST_3)
//                }
//                4 -> {
//                    mv.visitInsn(Opcodes.ICONST_4)
//                }
//                5 -> {
//                    mv.visitInsn(Opcodes.ICONST_5)
//                }
//                else -> {
//                    mv.visitVarInsn(Opcodes.BIPUSH, index)
//                }
//            }
            val type = parameterTypeList[index]
            println("parameter-type=${type}")
            when (type) {
                "Z" -> {
//                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Boolean::class.java),
//                        "valueOf",
//                        "(Z)Ljava/lang/Boolean;",
//                        false
//                    )
                    createStringBuidler("Z", "$type", ++localCount)
                }
                "C" -> {
//                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Character::class.java),
//                        "valueOf",
//                        "(C)Ljava/lang/Character;",
//                        false
//                    )
                    createStringBuidler("C", "$type", ++localCount)
                }
                "B" -> {
//                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Byte::class.java),
//                        "valueOf",
//                        "(B)Ljava/lang/Byte;",
//                        false
//                    )
                    createStringBuidler("B", "$type", ++localCount)
                }
                "S" -> {
//                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Short::class.java),
//                        "valueOf",
//                        "(S)Ljava/lang/Short;",
//                        false
//                    )
                    createStringBuidler("S", "$type", ++localCount)
                }
                "I" -> {
//                    mv.visitVarInsn(Opcodes.ILOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Integer::class.java),
//                        "valueOf",
//                        "(I)Ljava/lang/Integer;",
//                        false
//                    )
                    createStringBuidler("I", "$type", ++localCount)
                }
                "F" -> {
//                    mv.visitVarInsn(Opcodes.FLOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Float::class.java),
//                        "valueOf",
//                        "(F)Ljava/lang/Float;",
//                        false
//                    )
                    createStringBuidler("F", "$type", ++localCount)
                }
                "J" -> {
//                    mv.visitVarInsn(Opcodes.LLOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Long::class.java),
//                        "valueOf",
//                        "(L)Ljava/lang/Long;",
//                        false
//                    )
                    localCount++
                    createStringBuidler("J", "$type", ++localCount)
                }
                "D" -> {
//                    mv.visitVarInsn(Opcodes.DLOAD, ++localCount)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Double::class.java),
//                        "valueOf",
//                        "(D)Ljava/lang/Double;",
//                        false
//                    )
                    localCount++
                    createStringBuidler("D", "$type", ++localCount)
                }
                else -> {
//                    mv.visitVarInsn(Opcodes.ALOAD, ++localCount)
                    createStringBuidler("O", "$type", ++localCount)
                }
            }

            println("parameterTypeListSize=${parameterTypeList.size}----localCount=$localCount--type=$type")
//            mv.visitInsn(Opcodes.AASTORE)
        }

//        parameterIdentifier = newLocal(Type.LONG_TYPE)
//        mv.visitVarInsn(Opcodes.ASTORE, parameterIdentifier)
//        createStringBuidler("---", "type", 3)


//        mv.visitLdcInsn(methodId)
//        mv.visitMethodInsn(
//            Opcodes.INVOKESTATIC, Type.getInternalName(
//                Monitor::class.java
//            ), "pointNames", "(I)V", false
//        )

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
        println("life--&&&&&&&&&---visitLocalVariable")
        val methodParameterIndex = if (isStaticMethod) index else index - 1
        if (0 <= methodParameterIndex && methodParameterIndex < parameterTypeList.size) {
            Monitor.setMethodParameterGroup(methodId, name)
            println("visitLocalVariable--#####-methodId=$methodId----name=$name---methodParameterIndex=$methodParameterIndex")
        }
    }

    /**
     *
     * visitMaxs 表示方法的结束
     *
     * visitFieldInsn ： 访问某个成员变量的指令，支持GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
    visitFrame ：访问当前局部变量表和操作数栈中元素的状态，参数就是局部变量表和操作数栈的内容
    visitIincInsn ： 访问自增指令
    visitVarInsn ：访问局部变量指令，就是取局部变量变的值放入操作数栈
    visitMethodInsn ：访问方法指令，就是调用某个方法，支持INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
    visitInsn ： 访问无操作数的指令，例如nop，duo等等

    visitTypeInsn：访问type指令，即将一个类的全限定名作为参数然后new一个对象压入操作数栈中


    参数：
    type –该堆栈映射框架的类型。 对于扩展帧，必须为Opcodes.F_NEW ；对于压缩帧， Opcodes.F_SAME1为Opcodes.F_FULL ， Opcodes.F_APPEND ， Opcodes.F_CHOP ， Opcodes.F_SAME或Opcodes.F_APPEND ， Opcodes.F_SAME1 。
    numLocal –访问的帧中的局部变量数。
    local-此框架中的局部变量类型。 不得修改此数组。 基本类型由Opcodes.TOP ， Opcodes.INTEGER ， Opcodes.FLOAT ， Opcodes.LONG ， Opcodes.DOUBLE ， Opcodes.NULL或Opcodes.UNINITIALIZED_THIS （long和double由单个元素表示）。 引用类型由String对象（表示内部名称）表示，未初始化的类型由Label对象表示（此标签指定创建此未初始化值的NEW指令）。
    numStack –访问的帧中操作数堆栈元素的数量。
    堆栈–此帧中的操作数堆栈类型。 不得修改此数组。 它的内容与“本地”数组具有相同的格式
     *
     */
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {

        println("life--&&&&&&&&&---visitMaxs")

        /**
         * 进入这里表示方法结束了，我们要访问局部变量表里存储的变量的信息
         * visitFrame ：访问当前局部变量表和操作数栈中元素的状态，参数就是局部变量表和操作数栈的内容
         */


        val numLocal = (if (isStaticMethod) 0 else 1) + parameterTypeCount

        val localObjs = arrayOfNulls<Any>(numLocal)

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

        mv.visitFrame(
            Opcodes.F_FULL,
            numLocal,
            localObjs,
            1,
            localObjs
        )


//        //标志：try块结束
//        mv.visitLabel(to)
//        //标志：catch块开始位置
//        mv.visitLabel(target)
//
//
//
//        // 设置visitFrame：mv.visitFrame(Opcodes.F_FULL, 4, new Object[]{"java/lang/String", Opcodes.INTEGER, Opcodes.LONG, "[Ljava/lang/Object;"}, 1, new Object[]{"java/lang/Exception"});
//        val nLocal = (if (isStaticMethod) 0 else 1) + parameterTypeCount + if (parameterTypeCount == 0) 1 else 2
//        val localObjs = arrayOfNulls<Any>(nLocal)
//        var objIdx = 0
//        if (!isStaticMethod) {
//            localObjs[objIdx++] = className
//        }
//        for (parameter in parameterTypeList) {
//            when (parameter) {
//                "Z" -> {
//                    localObjs[objIdx++] = Opcodes.INTEGER
//                }
//                "C" -> {
//                    localObjs[objIdx++] = Opcodes.INTEGER
//                }
//                "B" -> {
//                    localObjs[objIdx++] = Opcodes.INTEGER
//                }
//                "S" -> {
//                    localObjs[objIdx++] = Opcodes.INTEGER
//                }
//                "I" -> {
//                    localObjs[objIdx++] = Opcodes.INTEGER
//                }
//                "F" -> {
//                    localObjs[objIdx++] = Opcodes.FLOAD
//                }
//                "J" -> {
//                    localObjs[objIdx++] = Opcodes.LONG
//                }
//                "D" -> {
//                    localObjs[objIdx++] = Opcodes.DOUBLE
//                }
//                else -> {
//                    localObjs[objIdx++] = parameter
//                }
//            }
//        }
//        localObjs[objIdx++] = Opcodes.LONG
//        if (parameterTypeCount > 0) {
//            localObjs[objIdx] = "[Ljava/lang/Object;"
//        }
//        mv.visitFrame(
//            Opcodes.F_FULL,
//            nLocal,
//            localObjs,
//            1,
//            arrayOf<Any>("java/lang/Exception")
//        )
//
//        // 异常信息保存到局部变量
//
//        // 异常信息保存到局部变量
//        val local = newLocal(Type.LONG_TYPE)
//        println("xxxx:$local")
//        mv.visitVarInsn(Opcodes.ASTORE, local)
//
//        // 输出参数
//
//        // 输出参数
//        mv.visitVarInsn(Opcodes.LLOAD, startTimeAddress)
//        mv.visitLdcInsn(methodId)
//        if (parameterTypeList.isEmpty()) {
//            mv.visitInsn(Opcodes.ACONST_NULL)
//        } else {
//            mv.visitVarInsn(Opcodes.ALOAD, parameterIdentifier)
//        }
//        mv.visitVarInsn(Opcodes.ALOAD, local)
//        mv.visitMethodInsn(
//            Opcodes.INVOKESTATIC, Type.getInternalName(
//                Monitor::class.java
//            ), "point", "(JI[Ljava/lang/Object;Ljava/lang/Throwable;)V", false
//        )
//
//        // 抛出异常
//        mv.visitVarInsn(Opcodes.ALOAD, local)
//        mv.visitInsn(Opcodes.ATHROW)
//

        super.visitMaxs(maxStack, maxLocals)
        probeMethodParameter()

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
        println("life--&&&&&&&&&---onMethodExit")
        probeMethodParameter()
        super.onMethodExit(opcode)

//        if ((Opcodes.IRETURN<=opcode&&opcode<= Opcodes.RETURN)||opcode== Opcodes.ATHROW){
//            probeMethodReturn(opcode)
//            mv.visitVarInsn(Opcodes.LLOAD,startTimeAddress)
//            println("startTimeAddress=${startTimeAddress}")
//            mv.visitLdcInsn(methodId)
//            if (parameterTypeList.isEmpty()){
//                mv.visitInsn(Opcodes.ACONST_NULL)
//            }else{
//                mv.visitVarInsn(Opcodes.ALOAD,parameterIdentifier)
//                println("parameterIdentifier=${parameterIdentifier}")
//            }
//
//            when(opcode){
//                Opcodes.RETURN->{
//                    mv.visitInsn(Opcodes.ACONST_NULL)
//                }
//                Opcodes.IRETURN->{
//
//                    mv.visitVarInsn(Opcodes.ILOAD, currentLocal)
//                    mv.visitMethodInsn(
//                        Opcodes.INVOKESTATIC,
//                        Type.getInternalName(Int::class.java),
//                        "valueOf",
//                        "(I)Ljava/lang/Integer;",
//                        false
//                    )
//                }
//                else->{
//                    mv.visitVarInsn(Opcodes.ALOAD, currentLocal)
//                }
//            }
//
//            println("currentLocal：$currentLocal")
//            //test
//            mv.visitMethodInsn(
//                Opcodes.INVOKESTATIC, Type.getInternalName(
//                    Monitor::class.java
//                ), "point", "(JI[Ljava/lang/Object;Ljava/lang/Object;)V", false
//            )
//        }

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
        println("life--&&&&&&&&&---visitAnnotation")
        println("annotation----222---methodVisitor--descriptor=$descriptor--visible=$visible")
        return super.visitAnnotation(descriptor, visible)
    }




}