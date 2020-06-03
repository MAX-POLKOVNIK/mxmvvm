package com.github.polkovnik.max.mvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BindableRecyclerViewAdapter<TData, TViewHolder>
    : RecyclerView.Adapter<TViewHolder>() where TViewHolder: BindableViewHolder<TData>
{
    var items = emptyList<TData>()

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TViewHolder, position: Int) =
        holder.bind(items[position])

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <TData, TViewHolder: BindableViewHolder<TData>> create(
            @LayoutRes resId: Int,
            init: (holder: BindableViewHolder<TData>) -> Unit = { },
            bind: (value: TData, view: View) -> Unit
        ): BindableRecyclerViewAdapter<TData, TViewHolder> =
            object : BindableRecyclerViewAdapter<TData, TViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TViewHolder {
                    return LayoutInflater.from(parent.context).inflate(resId, parent, false)
                        .let {  BindableViewHolder.create(it, bind, init) as TViewHolder }
                }
            }
    }
}