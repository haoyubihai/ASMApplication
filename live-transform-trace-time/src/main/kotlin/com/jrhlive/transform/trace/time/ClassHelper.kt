package com.jrhlive.transform.trace.time

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LocalVariableNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.ParameterNode


object ClassHelper {

    var classNode: ClassNode?=null

    fun initClassNode(classNode: ClassNode){
        this.classNode=classNode
    }

    fun methodNodes(classNode: ClassNode):List<MethodNode>{
        return classNode.methods
    }

    fun generateMethodKey(methodNode: MethodNode):String{
        return createKey(methodNode.name,methodNode.desc,methodNode.signature)
    }

    fun getMethodLocalVariables(classNode: ClassNode?,name:String,desc:String,signature: String?):List<LocalVariableNode>?{
        println("^^^^^classNode=$classNode----name=$name---desc=$desc---signature=$signature")
        if (classNode==null)return null
        val key = createKey(name, desc, signature)
        val methodNodes = methodNodes(classNode)
        println("^^^^^classNode=$classNode----name=$name---desc=$desc---signature=$signature-----------key=$key--methodsSize=${methodNodes.size}")
        if (methodNodes.isNotEmpty()){
            methodNodes.forEach { methodNode->
               val mKey =  generateMethodKey(methodNode)
                println("^^^^^mKey=$mKey----key=$key--methodNode=$methodNode")
                if (key == mKey){
                    println("^^^^^mKey=key------methodNode.localVariables.size=${methodNode.localVariables?.size}---paramsSize=${methodNode.parameters?.size}")
                    return methodNode.localVariables.sortedBy { it.index }
                }

                printlnParams(methodNode.parameters)
                printVariables(methodNode.localVariables)
            }
        }

        return null
    }

    private fun printlnParams(parameters: List<ParameterNode>?) {
        parameters?.onEach {
            println("^^^^&&&&---name=${it.name}----")
        }
    }

    fun printVariables(v:List<LocalVariableNode>?){
        v?.onEach {
            println("^^^^&&&&---name=${it.name}----desc---${it.desc}---index=${it.index}")
        }
    }

    private fun createKey(name:String,desc:String,signature: String?):String{
     return "$name$desc$signature"
    }
}