package com.live.libasm

import java.io.File

/**
 * @param acceptType  ClassReader.EXPAND_FRAMES,ClassReader.SKIP_CODE,ClassReader.SKIP_DEBUG,ClassReader.SKIP_FRAMES
 */
abstract class BaseTransformFactory(private val transformClass: ITransform){

}