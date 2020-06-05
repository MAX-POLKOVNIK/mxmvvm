package com.github.polkovnik.max.mvvm.state.empty

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

open class ImageEmptyState(message: String, val image: Drawable) : EmptyState(message) {
    constructor(@StringRes stringResId: Int, @DrawableRes imageResId: Int, context: Context)
        : this(context.getString(stringResId), ContextCompat.getDrawable(context, imageResId)!!)
}