package com.jrhlive.plugin

import com.android.build.gradle.AppExtension
import com.jrhlive.libasm.interceptor.Intercept
import org.gradle.api.Plugin
import org.gradle.api.Project

class LiveTraceNPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def traceExt = project.extensions.create("traceExt", Intercept)
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new TraceCTransform("TraceTimePlugin",project.traceExt))
    }
}
