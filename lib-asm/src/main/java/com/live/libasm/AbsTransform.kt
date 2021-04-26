package com.live.libasm

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.objectweb.asm.ClassVisitor

abstract class AbsTransform(private val pluginName:String) : Transform() {

    private  val transFromClass by lazy { createDefaultTransformClass() }
    private val transformFactory by  lazy { TransFormFactory(createTransJarsFactory(transFromClass),createTransDirsFactory(transFromClass)) }

    open fun createTransDirsFactory(transFromClass:ITransform): ITransformDirs {

        return TransFormDirFactory(transFromClass)
    }

    open fun createTransJarsFactory(transFromClass:ITransform): ITransformJars {
        return TransFormJarFileFactory(transFromClass)
    }

    override fun getName() = pluginName
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        beforeTransform()
        internalTransform(transformInvocation)
        afterTransform()

    }

    fun beforeTransform() {
    }

     open fun internalTransform(transformInvocation: TransformInvocation?) {
        if (transformInvocation != null) {
            transformInvocation.outputProvider.deleteAll()
            for (transformInput in transformInvocation.inputs) {
                for (jarInput in transformInput.jarInputs) {
                    transformFactory.transformJars(jarInput, transformInvocation.outputProvider, transformInvocation.isIncremental)
                }
                for (directoryInput in transformInput.directoryInputs) {
                    transformFactory.transformDirectory(directoryInput, transformInvocation.outputProvider, transformInvocation.isIncremental)
                }
            }
        }
    }

    fun afterTransform() {

    }

    abstract fun createDefaultTransformClass():ITransform

}