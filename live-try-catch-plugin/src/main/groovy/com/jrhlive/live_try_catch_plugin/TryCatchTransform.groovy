package com.jrhlive.live_try_catch_plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager

class TryCatchTransform extends  Transform {
    @Override
    String getName() {
        return "TryCatchPlugin"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }
}
