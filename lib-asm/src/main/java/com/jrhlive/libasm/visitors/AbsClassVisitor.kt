package com.jrhlive.libasm.visitors

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

abstract class AbsClassVisitor(private val api:Int,private val classVisitor: ClassVisitor) :ClassVisitor(api,classVisitor){

}