package com.jrhlive.plugin

import com.jrhlive.transform.trace.time.TraceTimeClassVisitor
import com.live.libasm.AbsTransFormClassFactory
import org.jetbrains.annotations.NotNull
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class TransFormClassFactory extends  AbsTransFormClassFactory{
    @Override
    void docClassReaderAccept(@NotNull byte[] sourceBytes, @NotNull ClassReader classReader, @NotNull ClassWriter classWriter) {
        Map<String ,List<String>> paramsMap =  MethodParamNamesScaner.getParamNames(new ByteArrayInputStream(sourceBytes))
        def classVisitor=new TraceTimeClassVisitor(classWriter,paramsMap)
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES)

    }
}
