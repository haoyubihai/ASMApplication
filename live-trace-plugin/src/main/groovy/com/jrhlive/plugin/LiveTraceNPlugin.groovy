package com.jrhlive.plugin

import com.android.build.gradle.AppExtension
import com.jrhlive.plugin.extensions.TraceTimeExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LiveTraceNPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("traceTime", TraceTimeExtension)
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new TraceTransform(project))
    }
}
