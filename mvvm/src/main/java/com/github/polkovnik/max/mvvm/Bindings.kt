package com.github.polkovnik.max.mvvm

import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

fun LifecycleOwner.bindCheckableTwoWay(liveData: Checkable, compoundButton: CompoundButton) {
    liveData.observe(this, Observer {
        if (compoundButton.isChecked != it) {
            compoundButton.isChecked = it ?: false
        }
    })

    compoundButton.setOnCheckedChangeListener { _, isChecked -> liveData.mutableValue = isChecked }
}

fun LifecycleOwner.bindCheckable(liveData: Checkable, compoundButton: CompoundButton) {
    liveData.observe(this, Observer {
        if (compoundButton.isChecked != it) {
            compoundButton.isChecked = it ?: false
        }
    })
}

fun LifecycleOwner.bindEnabled(liveData: Enabled, textView: TextView) =
    liveData.observe(this, Observer { textView.isEnabled = it ?: false })

fun LifecycleOwner.bindEnabled(liveData: Enabled, button: ImageButton) =
    liveData.observe(this, Observer { button.isEnabled = it ?: false })

fun LifecycleOwner.bindEnabled(liveData: Enabled, button: Button) =
    liveData.observe(this, Observer { button.isEnabled = it ?: false })

fun LifecycleOwner.bindEnabled(liveData: Enabled, frameLayout: FrameLayout) =
    liveData.observe(this, Observer { frameLayout.isEnabled = it ?: false })

fun LifecycleOwner.bindVisible(liveData: Visible, view: View, asInvisible: Boolean = false) =
    liveData.observe(this, Observer {
        when {
            it -> view.visibility = View.VISIBLE
            asInvisible -> view.visibility = View.INVISIBLE
            else -> view.visibility = View.GONE
        }
    })

fun LifecycleOwner.bindCommand(command: Command, block: () -> Unit) =
    command.observe(this, Observer { block.invoke() })

fun <T>LifecycleOwner.bindCommand(command: TCommand<T>, block: (T) -> Unit) =
    command.observe(this, Observer(block))

fun LifecycleOwner.bindProgress(liveData: Progress, ratingBar: RatingBar) {
    liveData.observe(this, Observer { ratingBar.rating = it })
    ratingBar.setOnRatingBarChangeListener { _, rating, _ -> liveData.mutableValue = rating }
}

@Suppress("UNCHECKED_CAST")
fun <T>LifecycleOwner.bindList(
    liveData: DataList<T>,
    recyclerView: RecyclerView,
    @LayoutRes resId: Int,
    init: (holder: BindableViewHolder<T>) -> Unit = { },
    bind: (value: T, view: View) -> Unit
) {
    liveData.observe(this, Observer {

        var adapter = recyclerView.adapter as? BindableRecyclerViewAdapter<T, BindableViewHolder<T>>
        if (adapter == null) {
            adapter = BindableRecyclerViewAdapter.create(resId, init, bind)
            recyclerView.adapter = adapter
        }

        adapter.items = it
        adapter.notifyDataSetChanged()
    })
}

fun <T>LifecycleOwner.bindDataToAction(liveData: Data<T>, block: (T) -> Unit) =
    liveData.observe(this, Observer(block))

fun <T>LifecycleOwner.bindDataToAction(liveData: DataList<T>, block: (List<T>) -> Unit) =
    liveData.observe(this, Observer(block))

fun LifecycleOwner.bindTitle(liveData: Text, toolbar: androidx.appcompat.widget.Toolbar) =
    liveData.observe(this, Observer { toolbar.title = it })

fun LifecycleOwner.bindText(liveData: Text, textView: TextView) =
    liveData.observe(this, Observer { textView.text = it })

fun LifecycleOwner.bindMenuItemVisibility(liveData: Visible, menuItem: MenuItem) =
    liveData.observe(this, Observer { menuItem.isVisible = it })

fun LifecycleOwner.bindTextTwoWay(liveData: Text, editText: EditText) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            liveData.mutableValue = s?.toString() ?: ""
        }
    })

    liveData.observe(this, Observer {
        if (editText.text.toString() == it) {
            return@Observer
        }

        val oldSelection = editText.selectionStart
        val newLength = it?.length ?: 0
        val oldLength = editText.text?.length ?: 0
        val diff = newLength - oldLength
        editText.setText(it)

        var newSelection = when (diff) {
            1, -1 -> oldSelection + diff
            else -> newLength
        }

        if (newSelection < 0) {
            newSelection = 0
        }

        try {
            editText.setSelection(newSelection)
        } catch (e: Exception) {
            print(e)
        }
    })
}

private var <T> ViewModel.MutableBindingProperty<T>.mutableValue: T
    get() = liveData.value!!
    set(value) {
        (liveData as MutableLiveData<T>).value = value
    }