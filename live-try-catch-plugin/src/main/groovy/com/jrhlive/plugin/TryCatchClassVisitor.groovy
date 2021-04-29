package com.jrhlive.plugin

import com.live.libasm.interceptor.Intercept
import com.live.libasm.util.HandClassUtils
import com.live.libasm.visitors.AbsClassVisitor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class TryCatchClassVisitor extends AbsClassVisitor {


    private int mApi = Opcodes.ASM8
    private Intercept intercept


    TryCatchClassVisitor(int api, @NotNull ClassVisitor classVisitor,Intercept intercept) {
        super(api, classVisitor)
        this.mApi = api
        this.intercept = intercept
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        def methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (HandClassUtils.INSTANCE.isHandMethod(name,intercept)) {
            methodVisitor = new TryCatchMethodVisitor(mApi,methodVisitor,access,name,descriptor)
        }
        return methodVisitor
    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return super.visitAnnotation(descriptor, visible)
    }
}
