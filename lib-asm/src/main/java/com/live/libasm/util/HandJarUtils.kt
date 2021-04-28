package com.live.libasm.util

object HandJarUtils {

    fun isHandJarFileName(name:String):Boolean{
        AnallyUtil.appendFile("-------------------------------isHandJarFileName=$name\n")
        return when{
            name.startsWith("com/jrhlive")-> {
                true
            }
            else -> false

        }
    }

    fun isHandJarClassName(className:String):Boolean{

        AnallyUtil.appendFile("############################isHandJarClassName=$className\n")
        return try {
            when{
                className.startsWith("R$")-> false
                className=="R"->false
                className.startsWith("BuildConfig")->false

                else -> true

            }
        }catch (e:Exception) {
            false

        }

    }
}