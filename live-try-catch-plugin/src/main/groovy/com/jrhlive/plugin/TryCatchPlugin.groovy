package com.jrhlive.plugin

import com.android.build.gradle.AppExtension
import com.jrhlive.libasm.interceptor.Intercept
import org.gradle.api.Plugin
import org.gradle.api.Project

class TryCatchPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def tryCatchExt = project.extensions.create("tryCatchExt", Intercept)
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new TryCatchTransform("TryCatchPlugin",project.tryCatchExt))
    }
}
