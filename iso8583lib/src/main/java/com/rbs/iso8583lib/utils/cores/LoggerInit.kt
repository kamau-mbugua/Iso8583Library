package com.rbs.iso8583lib.utils.cores

import android.content.Context
import timber.log.Timber

object LoggerInit {
    fun init(debug: Boolean = false) {
        if (debug) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    val filename = element.fileName.substringBefore(".")
                    return "RBS--:$filename.${element.methodName}() - L${element.lineNumber}"
                }
            })
        }
    }
}
