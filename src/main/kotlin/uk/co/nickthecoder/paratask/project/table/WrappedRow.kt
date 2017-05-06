package uk.co.nickthecoder.paratask.project.table

import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.beans.value.ObservableValueBase

class WrappedRow<R>(val row: R) {

    val codeProperty = SimpleStringProperty("")

    var code: String
        get() = codeProperty.get()
        set(value) {
            codeProperty.set(value)
        }


    val observables = mutableMapOf<String, ObservableValue<Any?>>()

    fun clearOption() {
        code = ""
    }

    fun observable(name: String, getter: (R) -> Any?): ObservableValue<Any?> {
        return observables.get(name) ?: cache(name, getter)
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