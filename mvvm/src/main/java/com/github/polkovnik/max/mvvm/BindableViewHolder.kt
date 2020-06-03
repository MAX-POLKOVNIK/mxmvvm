package com.github.polkovnik.max.mvvm

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: T)

    companion object {
        fun <TData> create(
            itemView: View,
            bind: (value: TData, view: View) -> Unit,
            init: (holder: BindableViewHolder<TData>) -> Unit = { }
        ) =
            object : BindableViewHolder<TData>(itemView) {
                init { init(this) }
                override fun bind(data: TData) = bind(data, this.itemView)
            }
    }
}