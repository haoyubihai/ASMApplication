package com.live.libasm.simple

import com.live.libasm.BaseTransformFactory
import com.live.libasm.ITransform
import com.live.libasm.ITransformJars


import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.live.libasm.util.LogUtil
import java.io.File


class SimpleTransformJarfileFactory(private val transformClass: ITransform) : BaseTransformFactory(transformClass),
    ITransformJars {
    override fun transformJars(jarInput: JarInput, outputProvider: TransformOutputProvider, isIncremental: Boolean) {
        val jarName = jarInput.name
        val status = jarInput.status
        val destFile = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        if (isIncremental) {
            when (status) {
                Status.ADDED -> {
                    handleJarFile(jarInput, destFile)
                }
                Status.CHANGED -> {
                    handleJarFile(jarInput, destFile)
                }
                Status.REMOVED -> {
                    if (destFile.exists()) {
                        destFile.delete()
                    }
                }
                Status.NOTCHANGED -> {

                }
                else -> {
                }
            }
        } else {
            handleJarFile(jarInput, destFile)
        }
    }

    override fun handleJarFile(jarInput: JarInput, destFile: File) {

        // 空的 jar 包不进行处理
        if (jarInput.file == null || jarInput.file.length() == 0L) {
            LogUtil.info("handleJarFile, ${jarInput.file.absolutePath} is null")
            return
        }

        FileUtils.copyFile(jarInput.file,destFile)

    }
}