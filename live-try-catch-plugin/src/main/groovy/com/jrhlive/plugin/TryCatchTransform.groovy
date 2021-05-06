package com.jrhlive.plugin

import com.jrhlive.libasm.AbsTransform
import com.jrhlive.libasm.ITransform
import com.jrhlive.libasm.interceptor.Intercept
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
