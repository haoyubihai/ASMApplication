package com.jrhlive.plugin

import com.jrhlive.libasm.factories.AbsTransFormClassFactory
import com.jrhlive.libasm.interceptor.Intercept
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class TryCatchClassFactory extends AbsTransFormClassFactory{
    TryCatchClassFactory(@Nullable Intercept intercept) {
        super(intercept)
    }

    @Override
    void docClassReaderAccept(@NotNull byte[] sourceBytes, @NotNull ClassReader classReader, @NotNull ClassWriter classWriter) {
        classReader.accept(new TryCatchClassVisitor(Opcodes.ASM8,classWriter,intercept),ClassReader.EXPAND_FRAMES)
    }

    @Override
    Intercept interceptor() {
        return intercept
    }
}
