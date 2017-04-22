package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import uk.co.nickthecoder.paratask.ParameterException

abstract class ValueParameter<T>(name: String, var required: Boolean = false)
    : AbstractParameter(name) {

    val property = SimpleObjectProperty<T>(this, "value")

    var value: T? = null
        set(v) {
            val changed = field != v
            field = v
            property.set(field)
            if (changed) {
                fireChanged()
            }
        }

    open fun errorMesssage(): String? {
        return errorMessage(value)
    }

    open fun errorMessage(v: T?): String? {
        if (required && v == null) {
            return "Required"
        }
        return null
    }

    fun check(v: T?) {
        val error = errorMessage(v)
        if (error != null) {
            throw ParameterException(this, error)
        }
    }

    /**
     * Generate a String representation of the Parameter's current value.
     * Null values are represented as an empty string.
     */
    open fun getStringValue(): String {
        return if (value == null) "" else value.toString()
    }

    /**
     * Sets the Parameter's value using a string representation of the value.
     * This is used when passing a value from the command line, and can also be used
     * when loading parameter values from an external source, such as from a JSON representation of a Task.
     */
    abstract fun setStringValue(s: String)
}
