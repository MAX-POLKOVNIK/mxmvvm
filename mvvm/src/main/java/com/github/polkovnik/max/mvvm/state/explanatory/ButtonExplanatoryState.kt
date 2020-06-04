package com.github.polkovnik.max.mvvm.state.explanatory

import android.content.Context
import androidx.annotation.StringRes

open class ButtonExplanatoryState(
    title: String,
    message: String,
    buttonText: String,
    buttonAction: () -> Unit
) : ExplanatoryState(title, message) {
    constructor(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
        @StringRes buttonTextResId: Int,
        buttonAction: () -> Unit,
        context: Context
    ) : this(
        context.getString(titleResId),
        context.getString(messageResId),
        context.getString(buttonTextResId),
        buttonAction
    )
}