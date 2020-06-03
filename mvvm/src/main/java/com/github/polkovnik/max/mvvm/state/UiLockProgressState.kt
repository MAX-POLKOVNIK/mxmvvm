package com.github.polkovnik.max.mvvm.state

import android.content.Context
import androidx.annotation.StringRes

data class UiLockProgressState(val message: String) : State {
    constructor(@StringRes stringResId: Int, context: Context) : this(context.getString(stringResId))
}