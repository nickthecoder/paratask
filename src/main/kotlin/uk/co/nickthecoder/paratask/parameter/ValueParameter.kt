package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import uk.co.nickthecoder.paratask.ParameterException

/**
 * The base class for all Parameters, which can hold a value.
 * A ValueParameter can be locked, which prevents its values from being changed.
 * If you attempt to change its value while locked, a ParameterException is thrown.
 * <p>
 * Locking is useful to ensure that a Task's parameters do not change after they have been checked, and before the
 * the Task has finished using them. This issue only occurs in a multi-threading environment, but even a simple
 * command line tool is multi-threaded when the Task is prompted. (The GUI will run in a separate thread from the Task's).
 * </p>
 */
abstract class ValueParameter<T>(name: String, var required: Boolean = false)
    : AbstractParameter(name) {

    /**
     * A Property, which can be used by GUI components to bind the value in the JavaFX controls with the value of the
     * parameter. This property is backed by {@link #value}.
     */
    val property = ValueProperty()

    var value: T? = null
        set(v) {
            if (locked) {
                throw ParameterException(this, "Locked")
            }

            val changed = field != v
            field = v
            if (changed) {
                fireChanged()
            }
        }

    var locked: Boolean = false

    override fun lock() {
        locked = true
    }

    override fun unlock() {
        locked = false
        value = value
        fireChanged()
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

    override fun check() {
        check(value)
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

    inner open class ValueProperty : SimpleObjectProperty<T>() {
        open override fun getValue(): T? {
            return this@ValueParameter.value
        }

        open override fun setValue(v: T?) {
            this@ValueParameter.value = v
        }
    }
}
