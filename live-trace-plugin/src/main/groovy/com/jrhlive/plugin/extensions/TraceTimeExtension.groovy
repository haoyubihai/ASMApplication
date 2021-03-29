package com.jrhlive.plugin.extensions
class TraceTimeExtension  {
      boolean enabled

      TraceTimeExtension() {
            enabled = true
      }


      @Override
      String toString() {
            return "TraceTimeExtension--enable---"+enabled
      }
}