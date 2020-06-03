package com.github.polkovnik.max.mvvm

import android.app.Application
import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.*
import com.github.polkovnik.max.mvvm.state.Idle
import com.github.polkovnik.max.mvvm.state.State
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

    val title = Text()
    val state = Data<State>(Idle)
    val closeCommand = Command()
    val showMessageCommand = TCommand<String>()
    val showAlertCommand =
        TCommand<Pair<String, String>>()

    open fun back() = closeCommand()

    open fun start() { }

    open fun stop() { }

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