package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.UiLockState

open class UiLockProgressLoadingState(message: String, progress: Float)
    : ProgressLoadingState(message, progress), UiLockState {
    constructor(@StringRes stringResId: Int, progress: Float, context: Context)
            : this(context.getString(stringResId), progress)
}