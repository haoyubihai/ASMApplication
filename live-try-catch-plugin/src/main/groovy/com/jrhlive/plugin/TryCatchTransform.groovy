package com.jrhlive.plugin

import com.live.libasm.AbsTransform
import com.live.libasm.ITransform
import org.jetbrains.annotations.NotNull

class TryCatchTransform extends  AbsTransform {

    TryCatchTransform(@NotNull String pluginName) {
        super(pluginName)
    }

    @Override
    ITransform createDefaultTransformClass() {
        return new TryCatchClassFactory()
    }
}
