package com.live.libasm.trans

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.utils.FileUtils
import com.live.libasm.ITransformJars
import org.apache.commons.codec.digest.DigestUtils
import java.io.File

class SimpleTransformJarFactory : ITransformJars {
    override fun transformJars(jarInput: JarInput, outputProvider: TransformOutputProvider, isIncremental: Boolean) {

        var jarName = jarInput.name
        val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
        if (jarName.endsWith(".jar")){
            jarName = jarName.substring(0,jarName.length-4)
        }
        val dest = outputProvider.getContentLocation("$jarName$md5Name",jarInput.contentTypes,jarInput.scopes,Format.JAR)

        handleJarFile(jarInput,dest)
    }

    override fun handleJarFile(jarInput: JarInput, destFile: File) {
        FileUtils.copyFile(jarInput.file,destFile)
    }
}