package com.github.polkovnik.max.mvvm.state.empty

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

open class ButtonImageEmptyState(
    message: String,
    image: Drawable,
    val buttonText: String,
    val buttonAction: () -> Unit
) : ImageEmptyState(message, image) {
    constructor(
        @StringRes messageResId: Int,
        @DrawableRes imageResId: Int,
        @StringRes buttonTextResId: Int,
        buttonAction: () -> Unit,
        context: Context
    ) : this(
        context.getString(messageResId),
        ContextCompat.getDrawable(context, imageResId)!!,
        context.getString(buttonTextResId),
        buttonAction
    )
}