package com.jrhlive.plugin

import com.jrhlive.transform.trace.time.TraceTimeClassVisitor
import com.live.libasm.AbsTransform
import com.live.libasm.ITransform
import com.live.libasm.ITransformDirs
import com.live.libasm.ITransformJars
import com.live.libasm.trans.SimpleTransformDirFactory
import com.live.libasm.trans.SimpleTransformJarFactory
import org.jetbrains.annotations.NotNull
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor

class TraceCTransform extends  AbsTransform {
    TraceCTransform(@NotNull String pluginName) {
        super(pluginName)
    }

    @Override
    ITransform createDefaultTransformClass() {
        return new TransFormClassFactory()
    }

}
