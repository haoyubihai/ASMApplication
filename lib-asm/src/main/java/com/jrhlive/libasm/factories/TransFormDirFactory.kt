package com.jrhlive.libasm.factories

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.jrhlive.libasm.ITransform
import com.jrhlive.libasm.ITransformDirs
import com.jrhlive.libasm.util.AnallyUtil
import com.jrhlive.libasm.util.AsmUtils
import com.jrhlive.libasm.util.HandClassUtils
import com.jrhlive.libasm.util.LogUtil
import java.io.File
import java.io.FileInputStream

class TransFormDirFactory(private val transformClass: ITransform):
    BaseTransformFactory(transformClass), ITransformDirs {
    override fun transformDirectory(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        val sourceFile = directoryInput.file
        val name = sourceFile.name
        val destDir = outputProvider.getContentLocation(name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
        LogUtil.info("TransformHelper[transformDirectory], name = $name, sourceFile Path = ${sourceFile.absolutePath}, destFile Path = ${destDir.absolutePath}, isIncremental = $isIncremental")
        if (isIncremental) {
            val changeFiles = directoryInput.changedFiles
            for (changeFile in changeFiles) {
                val status = changeFile.value
                val inputFile = changeFile.key
                val destPath = inputFile.absolutePath.replace(sourceFile.absolutePath, destDir.absolutePath)
                val destFile = File(destPath)
                LogUtil.info("目录：$destPath，状态：$status")
                when(status) {
                    Status.NOTCHANGED -> {

                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                    }
                    Status.CHANGED, Status.ADDED -> {
                        handleDirectory(inputFile, destFile)
                    }
                    else -> {}
                }
            }
        } else {
            // 首先全部拷贝，防止有后续处理异常导致文件的丢失
            FileUtils.copyDirectory(sourceFile, destDir)
            handleDirectory(sourceFile, destDir)
        }
    }

    override fun handleDirectory(sourceFile: File, destDir: File) {
        val files = sourceFile.listFiles { file, name ->
            if (file != null && file.isDirectory) {
                true
            } else {
                name!!.endsWith(".class")
            }
        }

        for (file in files!!) {
            try {
                val destFile = File(destDir, file.name)
                if (file.isDirectory) {
                    handleDirectory(file, destFile)
                } else {
                    val fileInputStream = FileInputStream(file)
                    val sourceBytes = AsmUtils.readBytes(fileInputStream)
                    var modifyBytes: ByteArray? = null
                    val fileName = file.name
                    AnallyUtil.appendFile("fileName-----$fileName")

                    if (HandClassUtils.isHandDirClassFile(sourceFile.absolutePath,fileName,transformClass.interceptor())) {
                        modifyBytes = transformClass.modifyClass(sourceBytes!!)
                    }
                    if (modifyBytes != null) {
                        val destPath = destFile.absolutePath
                        destFile.delete()
                        AnallyUtil.appendFile("\n\n\n^^^^^^^^^^^^handleDirectory---sourceFile=$sourceFile-fileName==$fileName------interceptor=-${transformClass.interceptor()?.packagePrefixs}")

                        AsmUtils.byte2File(destPath, modifyBytes)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


}