package com.jrhlive.plugin


import com.live.libasm.AbsTransform
import com.live.libasm.ITransform

import org.jetbrains.annotations.NotNull


class TraceCTransform extends  AbsTransform {
    TraceCTransform(@NotNull String pluginName) {
        super(pluginName)
    }

    @Override
    ITransform createDefaultTransformClass() {
        return new TransFormClassFactory()
    }

}
