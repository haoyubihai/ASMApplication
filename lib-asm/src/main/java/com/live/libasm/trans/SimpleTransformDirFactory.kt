package com.live.libasm.trans

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.live.libasm.ITransform
import com.live.libasm.ITransformDirs
import com.live.libasm.util.AsmUtils
import java.io.File
import java.io.FileInputStream

class SimpleTransformDirFactory(private val transFormClass: ITransform) : ITransformDirs {
    override fun transformDirectory(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider,
        isIncremental: Boolean
    ) {
        directoryInput.file?.let { file->
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    val fileName = it.name
                    if (fileName.endsWith(".class") && !fileName.startsWith("R\$")
                        && fileName != "BuildConfig.class" && fileName != "R.class"
                    ) {
                        val fileInputStream = FileInputStream(file)
                        val sourceBytes = AsmUtils.readBytes(fileInputStream)
                        sourceBytes?.let { byteArray ->
                            val modifyBytes = transFormClass.modifyClass(byteArray)
                            if (modifyBytes!=null){
                                println("modifyByte----${modifyBytes}")
                                val destPath = it.absolutePath
                                file.delete()
                                AsmUtils.byte2File(destPath,modifyBytes)
                            }
                        }
                    }
                }
                val dest = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                handleDirectory(file, dest)

            }
        }

    }

    override fun handleDirectory(sourceFile: File, destDir: File) {
      FileUtils.copyDirectory(sourceFile, destDir)
    }
}