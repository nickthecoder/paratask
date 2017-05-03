package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException

abstract class AbstractValue<T>(
        initialValue: T)

    : StringConverter<T>(), ParameterValue<T> {

    override val valueListeners = ValueListeners()

    var property = object : SimpleObjectProperty<T>() {
        override fun set(v: T) {
            val changed = v != get()
            if (changed) {
                valueListeners.fireChanged(this@AbstractValue)
                super.set(v)
            }
        }
    }

    override var value: T
        set(v: T) {
            property.set(v)
        }
        get() = property.get()

    init {
        value = initialValue
    }

    override var stringValue: String
        get() = toString(value)
        set(v: String) {
            value = fromString(v)
        }

    final fun errorMessage(): String? {
        return errorMessage(value)
    }

    /**
     * This is a convenience method, call the corresponding Parameter to do all of the error checking.
     */
    abstract fun errorMessage(v: T): String?

    override fun equals(other: Any?): Boolean {
        if (other is AbstractValue<*>) {
            return value == other.value
        }
        return false
    }

    override fun toString(): String = "Value name '${parameter.name}' = ${value}"

}