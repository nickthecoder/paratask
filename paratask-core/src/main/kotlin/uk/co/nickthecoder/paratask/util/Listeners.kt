/*
ParaTask Copyright (C) 2017  Nick Robinson>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.paratask.util

import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

open class Listeners<T : Any> : Iterable<T> {

    private var listeners = CopyOnWriteArrayList<WeakReference<T>>()

    private var strongListeners = CopyOnWriteArrayList<T>()

    val size: Int
        get() = listeners.size

    fun add(listener: T, weak: Boolean = true) {
        listeners.add(WeakReference(listener))
        if (!weak) {
            strongListeners.add(listener)
        }
    }

    open fun remove(listener: T) {
        listeners.forEach {
            if (it.get() === listener) {
                listeners.remove(it)
            }
        }
        strongListeners.remove(listener)
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
