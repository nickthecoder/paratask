package uk.co.nickthecoder.paratask.util

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

open class Listeners<T : Any> : Iterable<T> {

    private var listeners = CopyOnWriteArrayList<WeakReference<T>>()

    val size: Int
        get() = listeners.size

    fun add(listener: T) {
        listeners.add(WeakReference(listener))
    }

    open fun remove(listener: T) {
        listeners.forEach {
            if (it.get() === listener) {
                listeners.remove(it)
            }
        }
    }

    open fun forEach(action: (T) -> Unit) {
        listeners.forEach {
            val listener = it.get()
            if (listener == null) {
                listeners.remove(it)
            } else {
                action(listener)
            }
        }
    }

    override fun iterator(): Iterator<T> = listeners.map { it.get() }.filterNotNull().iterator()
}
