package com.jrhlive.plugin

import com.live.libasm.util.AnallyUtil
import com.live.libasm.util.CodeOptionUtil
import com.live.libasm.visitors.AbsMethodVisitorAdvice
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

class TryCatchMethodVisitor extends AbsMethodVisitorAdvice {

    private String descriptor=""
    private String methodName=""


    TryCatchMethodVisitor(int api, @Nullable MethodVisitor methodVisitor, int accessMethod, @NotNull String nameMethod, @NotNull String desc) {
        super(api, methodVisitor, accessMethod, nameMethod, desc)
        this.descriptor = desc
        this.methodName = nameMethod

    }
    Label from = new Label(),to = new Label(), target = new Label()

    @Override
    protected void onMethodEnter() {
        mv.visitTryCatchBlock(from, to, target, "java/lang/Exception")
        mv.visitLabel(from)
//        super.onMethodEnter()

    }

    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        //标志：try块结束
        mv.visitLabel(to)
        //标志：catch块开始位置
        mv.visitLabel(target)

//
        def local = newLocal(Type.getType("Ljava/lang/Exception;"))
        mv.visitVarInsn(Opcodes.ASTORE, local)

        mv.visitVarInsn(Opcodes.ALOAD, local)

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception",
                "printStackTrace", "()V", false);
//        mv.visitInsn(Opcodes.ATHROW)
//        mv.visitLabel(catchEnd)

        def returnCode = CodeOptionUtil.INSTANCE.getReturnCode(descriptor,mv)
        //判断方法的返回类型
        mv.visitInsn(returnCode)
        super.visitMaxs(maxStack, maxLocals)
    }
}
