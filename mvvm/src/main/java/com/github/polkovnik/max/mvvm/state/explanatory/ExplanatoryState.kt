package com.github.polkovnik.max.mvvm.state.explanatory

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.State

open class ExplanatoryState(title: String, message: String) : State() {
    constructor(@StringRes titleResId: Int, @StringRes messageResId: Int, context: Context)
            : this(context.getString(titleResId), context.getString(messageResId))
}