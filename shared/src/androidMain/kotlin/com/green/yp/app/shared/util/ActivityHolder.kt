package com.green.yp.app.shared.util

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityHolder {
    private var activityRef: WeakReference<Activity>? = null

    var currentActivity: Activity?
        get() = activityRef?.get()
        set(value) {
            activityRef = value?.let { WeakReference(it) }
        }
}
