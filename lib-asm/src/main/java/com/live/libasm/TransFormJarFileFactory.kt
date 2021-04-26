package com.live.libasm

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.live.libasm.util.LogUtil
import java.io.File

class TransFormJarFileFactory(private val transformClass: ITransform) : BaseTransformFactory(transformClass),
    ITransformJars {
    override fun transformJars(jarInput: JarInput, outputProvider: TransformOutputProvider, isIncremental: Boolean) {
        val jarName = jarInput.name
        val status = jarInput.status
        val destFile = outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        LogUtil.info("TransformHelper[transformJars], jar = $jarName, status = $status, isIncremental = $isIncremental")
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

        FileUtils.copyFile(jarInput.file, destFile)
//        // 构建 JarFile 文件
//        val modifyJar = JarFile(jarInput.file, false)
//        // 创建目标文件流
//        val jarOutputStream = JarOutputStream(FileOutputStream(destFile))
//        val enumerations = modifyJar.entries()
//        // 遍历 Jar 文件进行处理
//        for (jarEntry in enumerations) {
//            val inputStream = modifyJar.getInputStream(jarEntry)
//            val entryName = jarEntry.name
//            if (entryName.startsWith(".DSA") || entryName.endsWith(".SF")) {
//                return
//            }
//            val tempEntry = JarEntry(entryName)
//            jarOutputStream.putNextEntry(tempEntry)
//            var modifyClassBytes: ByteArray? = null
//            val destClassBytes = IOUtils.readBytes(inputStream)
//            if (!jarEntry.isDirectory && entryName.endsWith(".class") && !entryName.startsWith("android")) {
//                modifyClassBytes = destClassBytes?.let { modifyClass(it) }
//            }
//
//            if (modifyClassBytes != null) {
//                jarOutputStream.write(modifyClassBytes)
//            } else {
//                jarOutputStream.write(destClassBytes!!)
//            }
//            jarOutputStream.flush()
//            jarOutputStream.closeEntry()
//        }
//        jarOutputStream.close()
//        modifyJar.close()
    }
}