package com.jrhlive.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class LiveTraceNPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("begin --------------- TraceTimePlugin")
        def android = project.extensions.getByType(AppExtension.class)
        android?.registerTransform(new TraceTransform())
    }
}
