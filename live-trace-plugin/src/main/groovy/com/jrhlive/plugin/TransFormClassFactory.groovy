package com.jrhlive.plugin

import com.jrhlive.transform.trace.time.TraceTimeClassVisitor
import com.live.libasm.factories.AbsTransFormClassFactory
import com.live.libasm.interceptor.Intercept
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class TransFormClassFactory extends  AbsTransFormClassFactory{


    TransFormClassFactory(@Nullable Intercept intercept) {
        super(intercept)
    }

    @Override
    void docClassReaderAccept(@NotNull byte[] sourceBytes, @NotNull ClassReader classReader, @NotNull ClassWriter classWriter) {
        Map<String ,List<String>> paramsMap =  MethodParamNamesScaner.getParamNames(new ByteArrayInputStream(sourceBytes))
        def classVisitor=new TraceTimeClassVisitor(classWriter,paramsMap)
        classReader.accept(classVisitor,ClassReader.EXPAND_FRAMES)

    }

    @Override
    Intercept interceptor() {
        return intercept
    }
}
