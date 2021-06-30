
[一起玩转Android项目中的字节码（Transform篇)][https://cloud.tencent.com/developer/article/1378925]

1. project build.gradle 配置plugin路径

   ext {
     
        live_trace_version='0.1.0'
        trycatch_plugin_version = "0.1.0"
    }
    
   classpath "io.github.haoyubihai:live-trace-plugin:$live_trace_version"
   classpath "io.github.haoyubihai:live-try-catch-plugin:$trycatch_plugin_version"

2. module 的 build.gradle 引用plugin
    
 ////打印方法的入参信息
 apply plugin: 'live.trace.plugin'
 
 
 traceExt {
     //配置多包名 用","分割
     packagePrefixs = "com.jrhlive,"
     //配置类名，不配或者配置 "*" 表示所有类
     simpleClassNames = "*"
     //处理类型 "dir" "jar" "all" 是否区分jar包的class文件
     dealType = "all"
 
 }
 
 
 //对特定的方法添加try catch
 apply plugin: 'live.trycatch.plugin'
 
 tryCatchExt {
     //配置多包名 用","分割
     packagePrefixs = "com.jrhlive,"
     //配置类名，不配或者配置 "*" 表示所有类
     simpleClassNames = "Son,LibAsmClass"
     //对特地的方法添加try catch
     simpleMethodNames = "testTryCatch,pers"
 }
