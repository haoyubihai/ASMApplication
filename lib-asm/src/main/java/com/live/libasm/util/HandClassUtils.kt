package com.live.libasm.util

import com.live.libasm.interceptor.DealType
import com.live.libasm.interceptor.DefaultInterceptors
import com.live.libasm.interceptor.Intercept
import com.live.libasm.interceptor.InterceptorOptions

object HandClassUtils {

    fun isHandClassFile(classFileName: String?, intercept: Intercept? = null): Boolean {
        return if (classFileName.isNullOrEmpty() || intercept == null || !classFileName.endsWith(".class")) false
        else {
            if (!isDealJar(intercept.dealType))false
            else{
                val interceptorOption = interceptToOptions(intercept)
                interceptorOption?.run {
                    return packageJarPass(classFileName) && classJarPass(classFileName)
                }
                false
            }
        }

    }

    fun isHandDirClassFile(classFileName: String, className: String, intercept: Intercept? = null): Boolean {
        intercept?.let {
            if (!isDealDir(intercept.dealType)) return false
            if (className.endsWith(".class") && !className.startsWith("R\$")
                && className != "BuildConfig.class" && className != "R.class"
            ) {


                interceptToOptions(intercept)?.run {

                    return packageDirPass(classFileName) && classDirPass(className)
                }

            }
        }

        return false
    }


    private fun isDealDir(dealType: String): Boolean {

        return DealType.DIR.value == dealType || DealType.ALL.value==dealType
    }

    private fun isDealJar(dealType: String): Boolean {
        return DealType.JAR.value == dealType || DealType.ALL.value==dealType
    }


    private fun InterceptorOptions.packageJarPass(classFileName: String): Boolean {
        val className = AsmUtils.changeClassFileName(classFileName)
        packages.onEach { name ->
            if (className.startsWith(name)) {
                AnallyUtil.appendFile(
                    "ppppppppp------className=$className-------name=$name------className.startsWith(name)=${className.startsWith(
                        name
                    )}"
                )
                return true
            }
        }
        return false
    }

    /**
     * /Users/jiaruihua/project/ASMApplication/app/build/intermediates/javac/debug/classes/com/jrhlive/asmapplication-fileName==Base.class------interceptor=-com.jrhlive,
     */
    private fun InterceptorOptions.packageDirPass(classFileName: String): Boolean {
        val className = AsmUtils.changeClassFileName(classFileName)
        packages.onEach { name ->
            if (className.length >= name.length && className.contains(name)) {
                AnallyUtil.appendFile(
                    "ppppppppp------className=$className-------name=$name------className.startsWith(name)=${className.startsWith(
                        name
                    )}"
                )
                return true
            }
        }
        return false
    }

    private fun InterceptorOptions.classJarPass(classFileName: String): Boolean {
        //aa/aa/aa/MainActivity.class --> MainActivity
        val simpleClassName = AsmUtils.getClassFileName(classFileName)
        simpleClassNames.onEach { name ->
            AnallyUtil.appendFile("ccccccccccc-----------$name")
            if (filterDisabledInterceptors(simpleClassName)) return false
            if (simpleClassName == name || name == "*") {
                return true
            }
        }
        return false
    }

    /**
     * @param className MainActivity.class
     */
    private fun InterceptorOptions.classDirPass(className: String): Boolean {
        val simpleClassName = className.substring(0, className.lastIndexOf("."))
        simpleClassNames.onEach { name ->
            AnallyUtil.appendFile("ccccccccccc--className=$className--simpleClassName=$simpleClassName---------$name")
            if (filterDisabledInterceptors(simpleClassName)) return false
            if (simpleClassName == name || name == "*") {
                return true
            }
        }
        return false
    }


    fun InterceptorOptions.methodPass(methodName: String): Boolean {
        methodNames.onEach { name ->
            if (methodName == name || name == "*") {
                return true
            }
        }
        return false
    }

    private fun filterDisabledInterceptors(className: String): Boolean {
        return DefaultInterceptors.disableClassNamePrefixs.any { className.startsWith(it) }
    }

    private fun interceptToOptions(intercept: Intercept): InterceptorOptions? {
        intercept.run {
            val packages = packagePrefixs.fixConfigs().splitToSequence(",").toList().filter { it.isNotEmpty() }
            val classNames = simpleClassNames.fixConfigs().splitToSequence(",").toList().filter { it.isNotEmpty() }
            val methodNames = simpleMethodNames.fixConfigs().splitToSequence(",").toList().filter { it.isNotEmpty() }

            return InterceptorOptions(packages, classNames, methodNames)
        }
    }


    /**
     * 配置参数 非 * 的，后面没 "," 补上","
     */
    private fun fixConfigs(str: String): String {
        var p = str
        if (p != "*") {
            if (!p.endsWith(",")) {
                p = "$p,"
            }
        }
        return p
    }
}

fun String.fixConfigs(): String {
    var p = this
    if (p != "*") {
        if (!p.endsWith(",")) {
            p = "$p,"
        }
    }
    return p
}