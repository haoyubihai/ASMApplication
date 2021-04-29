package com.jrhlive.plugin

import com.live.libasm.AbsTransform
import com.live.libasm.ITransform
import com.live.libasm.interceptor.Intercept
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class TryCatchTransform extends  AbsTransform {


    TryCatchTransform(@NotNull String pluginName, @Nullable Intercept intercept) {
        super(pluginName, intercept)
    }

    @Override
    ITransform createDefaultTransformClass() {
        return new TryCatchClassFactory(intercept)
    }
}
