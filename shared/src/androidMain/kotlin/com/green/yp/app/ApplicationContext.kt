package com.green.yp.app

import android.content.Context

object ApplicationContext {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(): Context {
        return appContext ?: throw IllegalStateException("ApplicationContext not initialized")
    }
}