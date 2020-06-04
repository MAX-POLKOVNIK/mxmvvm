package com.github.polkovnik.max.mvvm.state.empty

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.State

open class EmptyState(val message: String) : State() {
    constructor(@StringRes stringResId: Int, context: Context) : this(context.getString(stringResId))
}