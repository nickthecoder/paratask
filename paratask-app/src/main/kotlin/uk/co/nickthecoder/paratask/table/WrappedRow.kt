/*
ParaTask Copyright (C) 2017  Nick Robinson

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

package uk.co.nickthecoder.paratask.table

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase

class WrappedRow<out R>(val row: R) {

    val codeProperty = SimpleStringProperty("")

    var code: String
        get() = codeProperty.get()
        set(value) {
            codeProperty.set(value)
        }

    /*
    init {
        codeProperty.addListener { observable, oldValue, newValue ->
            Thread.dumpStack()
            println("Code changed from $oldValue to $newValue")
        }
    }
    */

    val observables = mutableMapOf<String, ObservableValue<Any?>>()

    fun clearOption() {
        code = ""
    }

    fun observable(name: String, getter: (R) -> Any?): ObservableValue<Any?> {
        return observables[name] ?: cache(name, getter)
    }

    fun cache(name: String, getter: (R) -> Any?): ObservableValue<Any?> {
        val newO = MyObservableValue(getter(row))
        observables.put(name, newO)
        return newO
    }
}

class MyObservableValue(val wrappedValue: Any?) : ObservableValueBase<Any?>() {
    override fun getValue(): Any? = wrappedValue
}