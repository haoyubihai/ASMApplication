package com.jrhlive.libasm.util

import com.android.build.api.transform.Context
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.*
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

object AsmUtils {

    val extensions = arrayListOf<PlaceholderExtension>()

    fun readBytes(inputStream: InputStream): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).apply { len = this } != -1) {
                byteArrayOutputStream.write(buffer, 0, len)
            }
            byteArrayOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            byteArrayOutputStream.close()
            inputStream.close()
        }
        return null
    }

    fun byte2File(outputPath: String, sourceByte: ByteArray) {
        val file = File(outputPath)
        if (file.exists()) {
            file.delete()
        }

        val inputStream = ByteArrayInputStream(sourceByte)
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream.read(buffer).apply { len = this } != -1) {
            outputStream.write(buffer, 0, len)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    fun replaceInJar(context: Context, file: File): File? {
        val tempDir = context.temporaryDir
        if (isNeedModifyJar(file)) {
            modifyJar(file, tempDir, true)
        }
        return null
    }

    fun classFileDir(extension: PlaceholderExtension): String {
        return if (extension.classFileToReplace.isNotEmpty())
            extension.classFileToReplace.substring(0, extension.classFileToReplace.lastIndexOf("/"))
        else ""
    }

    private fun modifyJar(jarFile: File, tempDir: File?, nameHex: Boolean):File {

        /**
         * 读取原jar
         */
        val  file =  JarFile(jarFile)
        /** 设置输出到的jar */
        var hexName = ""
        if (nameHex) {
            hexName = DigestUtils.md5Hex(jarFile.absolutePath).substring(0, 8)
        }
        val outputJar =  File(tempDir, hexName + jarFile.name)
        val jarOutputStream =  JarOutputStream( FileOutputStream(outputJar))
        val enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry =  enumeration.nextElement()
            val inputStream = file.getInputStream(jarEntry)

            val entryName = jarEntry.name
            var className=""

            val zipEntry =  ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            var modifiedClassBytes:ByteArray? = null
            val  sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(".class")) {
                className = entryName.replace(".class", "")
                modifiedClassBytes = modifyClasses(className, sourceClassBytes)
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes)
            } else {
                jarOutputStream.write(modifiedClassBytes)
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        return outputJar

    }

    fun modifyClasses( className:String ,srcByteCode:ByteArray?):ByteArray? {
        val  modifyMap = modifyMap(className)
        var  classBytesCode:ByteArray? = null
        modifyMap?.let {
            try {
                println("--> start modifying $className")
                classBytesCode = modifyClass(srcByteCode, modifyMap, false)
                println("--> revisit modified $className")
                modifyClass(srcByteCode, modifyMap, true)
                println("--> finish modifying $className")
                return classBytesCode
            } catch ( e:Exception) {
                e.printStackTrace()
            }
        }

        if (classBytesCode == null) {
            classBytesCode = srcByteCode
        }
        return classBytesCode
    }

    private fun modifyClass(srcByteCode: ByteArray?, modifyMap: Map<String, String>, isOnlyVisit: Boolean): ByteArray? {
        val  classWriter =  ClassWriter(ClassWriter.COMPUTE_MAXS)
//        val  adapter =  ClassFilterVisitor(classWriter, modifyMap)
//        adapter.isOnlyVisit = isOnlyVisit
//        val cr =  ClassReader(srcByteCode)
//        cr.accept(adapter, 0)
        return classWriter.toByteArray()
    }

    fun  modifyMap( className:String?) :Map<String,String>?{
        if (className?.trim()?.isNotEmpty()==true){
            if (extensions.isEmpty()) return null
            val  values = mutableMapOf<String,String>()
            extensions.find {
                val name = String.format("%s/%s", classFileDir(it), classFileName(it))
                if (name == className) {
                    values.putAll(it.replaceValues)
                    true
                } else
                    false
            }
            return values
        }

        return null
    }

    //是否修改jar
    private fun isNeedModifyJar(jarFile: File): Boolean {
        if (extensions.isNullOrEmpty()) return false
        var modify = false
        val file = JarFile(jarFile)
        val entries = file.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            val isModify = extensions.any {
                val name = String.format("s%/%s", classFileDir(it), classFileNameWithClass(it))
                return if (name == entry.name) {
                    modify = true
                    true
                } else false

            }
            if (isModify) break
        }
        file.close()
        return modify
    }

    private fun classFileNameWithClass(extension: PlaceholderExtension): String? {
        return classFileName(extension) + ".class"
    }

    fun classFileName(extension: PlaceholderExtension?): String {
        extension?.classFileToReplace?.let {
            if (it.isNotEmpty()) {
                return it.substring(it.lastIndexOf("/") + 1, it.lastIndexOf("."))
            }

        }
        return ""
    }

    fun getClassFileName(filePath:String):String{
        try {
            if (filePath.isNotEmpty()){
                return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."))
            }
        }catch (e:Exception){

        }

        return  ""
    }


    fun changeClassFileName(fileName:String):String{
        return fileName.replace("/",".")
    }

}