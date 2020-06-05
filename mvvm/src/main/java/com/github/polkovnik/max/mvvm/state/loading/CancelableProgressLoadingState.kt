package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes

open class CancelableProgressLoadingState(
    message: String,
    progress: Float,
    cancelButtonText: String,
    cancelButtonAction: () -> Unit
) : ProgressLoadingState(message, progress) {
    constructor(
        @StringRes messageResId: Int,
        progress: Float,
        @StringRes cancelButtonTextResId: Int,
        cancelButtonAction: () -> Unit,
        context: Context
    ) : this(
        context.getString(messageResId),
        progress,
        context.getString(cancelButtonTextResId),
        cancelButtonAction
    )
}