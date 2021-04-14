package com.jrhlive.library

data class MethodDesc(val fullClassName:String,val simpleClassName:String,val methodName:String,val desc:String,val parameterTypeList:List<String>?,val returnType:String)