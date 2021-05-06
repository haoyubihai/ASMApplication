package com.jrhlive.libasm

import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import java.io.File

interface ITransformJars {
    fun transformJars(jarInput: JarInput, outputProvider: TransformOutputProvider, isIncremental: Boolean)
    fun handleJarFile(jarInput: JarInput, destFile: File)
}