package com.github.polkovnik.max.mvvm.state.loading

import android.content.Context
import androidx.annotation.StringRes
import com.github.polkovnik.max.mvvm.state.State

open class LoadingState(val message: String) : State() {
    constructor(@StringRes stringResId: Int, context: Context) : this(context.getString(stringResId))
}