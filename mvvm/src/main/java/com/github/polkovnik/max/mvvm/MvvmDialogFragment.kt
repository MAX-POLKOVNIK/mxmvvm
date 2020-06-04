package com.github.polkovnik.max.mvvm

import android.app.Activity
import android.app.ProgressDialog
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.github.polkovnik.max.mvvm.state.Idle
import com.github.polkovnik.max.mvvm.state.loading.UiLockLoadingState

abstract class MvvmDialogFragment<TViewModel> : DialogFragment() where TViewModel: ViewModel {

    @Suppress("DEPRECATION")
    private var progressDialog: ProgressDialog? = null

    protected abstract val viewModel: TViewModel

    override fun onResume() {
        super.onResume()

        viewModel.start()
    }

    override fun onPause() {
        super.onPause()

        viewModel.stop()
    }

    protected open fun bindViewModel() {
        bindCommand(viewModel.showMessageCommand) {
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        }

        bindDataToAction(viewModel.state) { uiLockBusyStateChanged() }

        bindCommand(viewModel.showAlertCommand) {
            AlertDialog.Builder(requireContext())
                .apply {
                    if (it.second.isNotBlank())
                        setTitle(it.second)
                }
                .setMessage(it.first)
                .setPositiveButton("OK", null)
                .show()
        }

        bindCommand(viewModel.closeCommand) { close() }
    }

    protected open fun close() = dismissAllowingStateLoss()

    @Suppress("DEPRECATION")
    protected open fun uiLockBusyStateChanged() {
        when (val state = viewModel.state.value) {
            is UiLockLoadingState -> {
                progressDialog = ProgressDialog(requireContext())
                    .apply { setMessage(state.message) }
                    .also { it.show() }
            }
            Idle -> {
                progressDialog?.dismiss()
                progressDialog = null
            }
        }
    }
}

val Fragment.parent: Any?
    get() = parentFragment ?: activity

inline fun <reified T> Fragment.castParent(): T? = parent as? T

inline fun <reified T> Fragment.findParentOfType(): T? {
    var parent = parent
    while (parent != null) {
        when (parent) {
            is T -> return parent
            is Activity -> return null
            is Fragment -> parent = parent.parent
        }
    }

    return null
}