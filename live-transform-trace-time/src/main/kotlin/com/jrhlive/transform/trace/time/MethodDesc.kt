package com.jrhlive.transform.trace.time

data  class MethodDesc(val fullClassName:String,val simpleClassName:String,val methodName:String,val desc:String,val parameterTypeList:List<String>?,val returnType:String)