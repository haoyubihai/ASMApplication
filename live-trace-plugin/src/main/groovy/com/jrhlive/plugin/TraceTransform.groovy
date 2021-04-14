package com.jrhlive.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.jrhlive.transform.trace.time.TraceTimeClassVisitor
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter

class TraceTransform extends Transform{
    Project project

    TraceTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return TraceTransform.class.getSimpleName()
    }

    /**
     * Returns the type(s) of data that is consumed by the Transform. This may be more than
     * one type.
     *
     * <strong>This must be of type {@link QualifiedContent.DefaultContentType}</strong>
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Returns the scope(s) of the Transform. This indicates which scopes the transform consumes.
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * Returns whether the Transform can perform incremental work.
     *
     * <p>If it does, then the TransformInput may contain a list of changed/removed/added files, unless
     * something else triggers a non incremental run.
     */
    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        super.transform(transformInvocation)

//        println("project.traceTime--${project.traceTime.enabled}")
//        if (project.traceTime!=null&&project.traceTime.enabled){
            transformInvocation.inputs.each {
                it.directoryInputs.each {
                    if(it.file.isDirectory()){
                        it.file.eachFileRecurse {
                            def fileName=it.name
                            if(fileName.endsWith(".class")&&!fileName.startsWith("R\$")
                                    && fileName != "BuildConfig.class"&&fileName!="R.class"&&!fileName.contains("TimeCost")){
                                //各种过滤类，关联classVisitor
                                handleFile(it)
                            }
                        }
                    }
                    def dest=transformInvocation.outputProvider.getContentLocation(it.name,it.contentTypes,it.scopes, Format.DIRECTORY)
                    FileUtils.copyDirectory(it.file,dest)
                }
                it.jarInputs.each { jarInput->
                    def jarName = jarInput.name
                    def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                }
            }
//        }
    }

    private void handleFile(File file){
        def cr=new ClassReader(file.bytes)
        def cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS)
        ClassNode classNode = new ClassNode()
        Map<String ,List<String>> paramsMap =  MethodParamNamesScaner.getParamNames(new FileInputStream(file))
        def classVisitor=new TraceTimeClassVisitor(cw,classNode,paramsMap)
        cr.accept(new CheckClassAdapter(Opcodes.ASM7, classNode, false) {}, ClassReader.EXPAND_FRAMES)
        cr.accept(classVisitor,ClassReader.EXPAND_FRAMES)
        def bytes=cw.toByteArray()




        //写回原来这个类所在的路径
        FileOutputStream fos=new FileOutputStream(file.getParentFile().getAbsolutePath()+File.separator+file.name)
        fos.write(bytes)
        fos.close()
    }
}