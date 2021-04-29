package com.jrhlive.plugin


import com.live.libasm.AbsTransform
import com.live.libasm.ITransform
import com.live.libasm.interceptor.Intercept
import com.live.libasm.util.AnallyUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


class TraceCTransform extends  AbsTransform {

    TraceCTransform(@NotNull String pluginName, @Nullable Intercept intercept) {
        super(pluginName, intercept)
    }

    @Override
    ITransform createDefaultTransformClass() {
        return new TransFormClassFactory(intercept)
    }
}
