package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes

open class ProgressLoadingState(message: String, val progress: Float) : LoadingState(message) {
    constructor(@StringRes stringResId: Int, progress: Float, context: Context)
            : this(context.getString(stringResId), progress)
}