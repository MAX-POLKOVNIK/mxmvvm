package com.github.polkovnik.max.mvvm

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.github.polkovnik.max.mvvm.state.Idle
import com.github.polkovnik.max.mvvm.state.State
import com.github.polkovnik.max.mvvm.state.empty.ButtonImageEmptyState
import com.github.polkovnik.max.mvvm.state.empty.EmptyState
import com.github.polkovnik.max.mvvm.state.empty.ImageEmptyState
import com.github.polkovnik.max.mvvm.state.explanatory.ButtonExplanatoryState
import com.github.polkovnik.max.mvvm.state.explanatory.ExplanatoryState
import com.github.polkovnik.max.mvvm.state.loading.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

typealias Text = ViewModel.TextProperty
typealias Checkable = ViewModel.CheckableProperty
typealias Data<T> = ViewModel.DataProperty<T>
typealias DataList<T> = ViewModel.DataListProperty<T>
typealias Enabled = ViewModel.EnabledProperty
typealias Progress = ViewModel.ProgressProperty
typealias Visible = ViewModel.VisibleProperty
typealias Command = ViewModel.LiveEvent
typealias TCommand<T> = ViewModel.SingleLiveEvent<T>

abstract class ViewModel(application: Application) : AndroidViewModel(application) {
    protected val context: Context by lazy { getApplication<Application>() }
    protected var errorDescriptionProvider: (Throwable) -> String = { it.toString() }
    protected var errorHandler: (Throwable) -> Unit = { Log.d("mxmvvm", "Error handled: $it") }

    val title = Text()
    val state = Data<State>(Idle)
    val closeCommand = Command()
    val showMessageCommand = TCommand<String>()
    val showAlertCommand = TCommand<Pair<String, String>>()

    open fun back() = closeCommand()

    open fun start() { }

    open fun stop() { }

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        this.getApplication<Application>().getString(resId, *formatArgs)

    protected fun showMessage(stringResourceId: Int, vararg formatArgs: Any) =
        showMessage(getString(stringResourceId, *formatArgs))

    protected fun showMessage(message: String) = showMessageCommand(message)

    protected fun showMessage(stringResourceId: Int) =
        showMessage(getApplication<Application>().getString(stringResourceId))

    protected fun processError(error: Throwable, showToast: Boolean = true) {
        errorHandler(error)

        if (showToast) {
            showMessage(errorDescriptionProvider(error))
        }
    }

    protected open fun showExplanatoryState(
        title: String,
        error: Throwable,
        buttonText: String? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        showExplanatoryState(title, errorDescriptionProvider(error), buttonText, buttonAction)
    }

    open fun showExplanatoryState(
        @StringRes titleResId: Int,
        error: Throwable,
        buttonText: String? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        showExplanatoryState(getString(titleResId), error, buttonText, buttonAction)
    }

    open fun showExplanatoryState(
        title: String,
        message: String,
        buttonText: String? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            buttonAction != null && buttonText != null ->
                ButtonExplanatoryState(title, message, buttonText, buttonAction)
            else ->
                ExplanatoryState(title, message)
        }
    }

    open fun showExplanatoryState(
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
        @StringRes buttonTextResId: Int? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            buttonAction != null && buttonTextResId != null ->
                ButtonExplanatoryState(titleResId, messageResId, buttonTextResId, buttonAction, context)
            else ->
                ExplanatoryState(titleResId, messageResId, context)
        }
    }

    open fun showLoadingState(
        isLockUi: Boolean,
        message: String,
        progress: Float? = null,
        cancelButtonText: String? = null,
        cancelButtonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            cancelButtonAction != null && cancelButtonText != null && progress != null && isLockUi ->
                UiLockCancelableProgressLoadingState(message, progress, cancelButtonText, cancelButtonAction)
            cancelButtonAction != null && cancelButtonText != null && progress != null && !isLockUi ->
                CancelableProgressLoadingState(message, progress, cancelButtonText, cancelButtonAction)
            progress != null && isLockUi ->
                UiLockProgressLoadingState(message, progress)
            progress != null && !isLockUi ->
                ProgressLoadingState(message, progress)
            isLockUi ->
                UiLockLoadingState(message)
            else ->
                LoadingState(message)
        }
    }

    open fun showLoadingState(
        isLockUi: Boolean,
        @StringRes messageResId: Int,
        progress: Float? = null,
        @StringRes cancelButtonTextResId: Int? = null,
        cancelButtonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            cancelButtonAction != null && cancelButtonTextResId != null && progress != null && isLockUi ->
                UiLockCancelableProgressLoadingState(messageResId, progress, cancelButtonTextResId, cancelButtonAction, context)
            cancelButtonAction != null && cancelButtonTextResId != null && progress != null && !isLockUi ->
                CancelableProgressLoadingState(messageResId, progress, cancelButtonTextResId, cancelButtonAction, context)
            progress != null && isLockUi ->
                UiLockProgressLoadingState(messageResId, progress, context)
            progress != null && !isLockUi ->
                ProgressLoadingState(messageResId, progress, context)
            isLockUi ->
                UiLockLoadingState(messageResId, context)
            else ->
                LoadingState(messageResId, context)
        }
    }

    open fun showEmptyState(
        message: String,
        image: Drawable? = null,
        buttonText: String? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            buttonText != null && buttonAction != null && image != null ->
                ButtonImageEmptyState(message, image, buttonText, buttonAction)
            image != null ->
                ImageEmptyState(message, image)
            else ->
                EmptyState(message)
        }
    }

    open fun showEmptyState(
        @StringRes messageResId: Int,
        @DrawableRes imageResId: Int? = null,
        @StringRes buttonTextResId: Int? = null,
        buttonAction: (() -> Unit)? = null
    ) {
        state.mutableValue = when {
            buttonTextResId != null && buttonAction != null && imageResId != null ->
                ButtonImageEmptyState(messageResId, imageResId, buttonTextResId, buttonAction, context)
            imageResId != null ->
                ImageEmptyState(messageResId, imageResId, context)
            else ->
                EmptyState(messageResId, context)
        }
    }

    class CheckableProperty(defaultValue: Boolean = false) : MutableBindingProperty<Boolean>(defaultValue)
    class DataListProperty<T>(defaultValue: List<T> = emptyList()) : MutableBindingProperty<List<T>>(defaultValue)
    class DataProperty<T>(defaultValue: T) : MutableBindingProperty<T>(defaultValue)
    class EnabledProperty(defaultValue: Boolean = false) : MutableBindingProperty<Boolean>(defaultValue)
    class ProgressProperty(defaultValue: Float = 0f) : MutableBindingProperty<Float>(defaultValue)
    class TextProperty(defaultValue: String = "") : MutableBindingProperty<String>(defaultValue)
    class VisibleProperty(defaultValue: Boolean = false) : MutableBindingProperty<Boolean>(defaultValue)

    abstract class MutableBindingProperty<T>(defaultValue: T) {
        private val mutableLiveData = MutableLiveData<T>()

        val liveData: LiveData<T> = mutableLiveData
        val value: T
            get() = liveData.value!!

        init { mutableLiveData.value = defaultValue }

        fun observe(owner: LifecycleOwner, observer: Observer<T>) = liveData.observe(owner, observer)
        fun observeForever(observer: (value: T) -> Unit) = liveData.observeForever { observer(it) }
    }

    class LiveEvent : () -> Unit {
        private val liveData =
            SingleMutableLiveData<Void>()

        fun observe(owner: LifecycleOwner, observer: Observer<Void>) = liveData.observe(owner, observer)

        @MainThread
        override fun invoke() { liveData.value = null }
    }

    class SingleLiveEvent<T> : (T) -> Unit {

        private val liveData =
            SingleMutableLiveData<T>()

        fun observe(owner: LifecycleOwner, observer: Observer<in T>) = liveData.observe(owner, observer)

        @MainThread
        override fun invoke(arg: T) { liveData.value = arg }
    }

    private class SingleMutableLiveData<T> :  MutableLiveData<T>() {

        private val pending = AtomicBoolean(false)

        @MainThread
        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, Observer {
                if (pending.compareAndSet(true, false)) {
                    observer.onChanged(it)
                }
            })
        }

        @MainThread
        override fun setValue(t: T?) {
            pending.set(true)
            super.setValue(t)
        }
    }

    protected var <T> MutableBindingProperty<T>.mutableValue: T
        get() = value
        set(value) {
            (liveData as MutableLiveData<T>).value = value
        }
}

