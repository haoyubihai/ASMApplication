package com.jrhlive.libasm.factories

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import com.jrhlive.libasm.ITransformDirs
import com.jrhlive.libasm.ITransformJars
import java.io.File


class TransFormFactory(private val transJars: ITransformJars, private val transDir: ITransformDirs) :
    ITransformJars,
    ITransformDirs {
    override fun transformJars(jarInput: JarInput, outputProvider: TransformOutputProvider, isIncremental: Boolean) {
        transJars.transformJars(jarInput, outputProvider, isIncremental)
    }

    override fun handleJarFile(jarInput: JarInput, destFile: File) {
       transJars.handleJarFile(jarInput, destFile)
    }

    override fun transformDirectory(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
       transDir.transformDirectory(directoryInput, outputProvider, isIncremental)
    }

    override fun handleDirectory(sourceFile: File, destDir: File) {
        transDir.handleDirectory(sourceFile, destDir)
    }


}