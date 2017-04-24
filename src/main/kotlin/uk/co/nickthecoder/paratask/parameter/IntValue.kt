package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException

class IntValue(override val parameter: IntParameter) : StringConverter<Int?>(), Value<Int?> {

    override val valueListeners = ValueListeners()

    var property = object : SimpleObjectProperty<Int?>() {
        override fun set(v: Int?) {
            val changed = v != get()
            if (changed) {
                valueListeners.fireChanged(this@IntValue)
                super.set(v)
            }
        }
    }

    override var value: Int?
        set(v: Int?) {
            property.set(v)
        }
        get() = property.get()

    override var stringValue: String
        get() = toString(value)
        set(v: String) {
            value = fromString(v)
        }

    override fun fromString(str: String): Int? {
        val trimmed = str.trim()

        if (trimmed.length == 0) {
            return null
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (e: Exception) {
            throw ParameterException(parameter, "Not an integer")
        }
    }

    override fun toString(obj: Int?): String {
        return obj?.toString() ?: ""
    }


    override fun errorMessage() = errorMessage(value)

    fun errorMessage(v: Int?) = parameter.errorMessage(v)
}