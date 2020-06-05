package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.UiLockState

class UiLockCancelableProgressLoadingState(
    message: String,
    progress: Float,
    cancelButtonText: String,
    cancelButtonAction: () -> Unit
) : CancelableProgressLoadingState(
    message,
    progress,
    cancelButtonText,
    cancelButtonAction
), UiLockState {
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