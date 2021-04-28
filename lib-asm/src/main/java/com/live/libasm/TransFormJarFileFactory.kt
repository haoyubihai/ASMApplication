package com.live.libasm

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.live.libasm.util.AnallyUtil
import com.live.libasm.util.AsmUtils
import com.live.libasm.util.HandJarUtils
import com.live.libasm.util.LogUtil
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

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

//        FileUtils.copyFile(jarInput.file,destFile)

        LogUtil.error("begin-----------------transJars---${jarInput.file.name}")
//        // 构建 JarFile 文件
        val modifyJar = JarFile(jarInput.file, false)

        AnallyUtil.appendFile("\n\n\n**********----------handjarfile=====${jarInput.file.name}************\n\n\n")
//        // 创建目标文件流
        val jarOutputStream = JarOutputStream(FileOutputStream(destFile))

        val enumeration = modifyJar.entries()

        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement()
            val inputStream = modifyJar.getInputStream(jarEntry)
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            jarOutputStream.putNextEntry(zipEntry)
            var modifyClassBytes: ByteArray? = null
            val sourceClassBytes = IOUtils.toByteArray(inputStream)
            var className = AsmUtils.getClassFileName(entryName)

            if (HandJarUtils.isHandJarFileName(entryName)&&entryName.endsWith(".class")&&HandJarUtils.isHandJarClassName(className)) {
                AnallyUtil.appendFile("$$$$$$$$$$$$$$$$---实际处理的文件 className = $className\n\n")
                modifyClassBytes = transformClass.modifyClass(sourceClassBytes)

            }
            if (modifyClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifyClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        modifyJar.close()
        LogUtil.error("end-----------------transJars-----${jarInput.file.name}")
    }
}