package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.State
import com.github.polkovnik.max.mvvm.state.UiLockState

open class UiLockLoadingState(message: String) : LoadingState(message), UiLockState {
    constructor(@StringRes stringResId: Int, context: Context) : this(context.getString(stringResId))
}