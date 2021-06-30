package com.jrhlive.libasm

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import java.io.File

interface ITransformDirs {
    fun transformDirectory(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider, isIncremental: Boolean)
    fun handleDirectory(sourceFile: File, destDir: File)
}