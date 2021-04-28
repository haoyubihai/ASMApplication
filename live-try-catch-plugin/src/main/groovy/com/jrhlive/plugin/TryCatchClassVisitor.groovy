package com.jrhlive.plugin

import com.live.libasm.visitors.AbsClassVisitor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TryCatchClassVisitor extends AbsClassVisitor {


    private int mApi = Opcodes.ASM8


    TryCatchClassVisitor(int api, @NotNull ClassVisitor classVisitor) {
        super(api, classVisitor)
        this.mApi = api
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        def methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name!="<init>"&& !name.startsWith("_\$") && null != methodVisitor) {
            methodVisitor = new TryCatchMethodVisitor(mApi,methodVisitor,access,name,descriptor)
        }
        return methodVisitor
    }

}
