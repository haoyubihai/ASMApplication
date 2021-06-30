package com.jrhlive.libasm.util

import org.gradle.api.Project

class PlaceholderExtension(private val project:Project) {
    var classFileToReplace = ""
    var isModifyJava = false
    val replaceValues = mutableMapOf<String,String>()

    override fun toString(): String {
        return "classFile=$classFileToReplace,isModifyJava=$isModifyJava,values>>$replaceValues"
    }
}