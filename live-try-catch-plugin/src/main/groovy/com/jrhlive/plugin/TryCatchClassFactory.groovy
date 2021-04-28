package com.jrhlive.plugin

import com.live.libasm.factories.AbsTransFormClassFactory
import org.jetbrains.annotations.NotNull
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class TryCatchClassFactory extends AbsTransFormClassFactory{
    @Override
    void docClassReaderAccept(@NotNull byte[] sourceBytes, @NotNull ClassReader classReader, @NotNull ClassWriter classWriter) {
        classReader.accept(new TryCatchClassVisitor(Opcodes.ASM8,classWriter),ClassReader.EXPAND_FRAMES)
    }
}
