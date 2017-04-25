package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException

class BooleanValue(
        override val parameter: BooleanParameter,
        initialValue: Boolean? = null)

    : StringConverter<Boolean?>(), Value<Boolean?> {

    override val valueListeners = ValueListeners()

    var property = object : SimpleObjectProperty<Boolean?>() {
        override fun set(v: Boolean?) {
            val changed = v != get()
            if (changed) {
                valueListeners.fireChanged(this@BooleanValue)
                super.set(v)
            }
        }
    }

    override var value: Boolean?
        set(v: Boolean?) {
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

    override fun fromString(str: String): Boolean? {
        val trimmed = str.trim()

        return when (trimmed) {
            "" -> null
            "true" -> true
            "false" -> false
            else -> throw ParameterException(parameter, "Expected 'true' or 'false'")
        }
    }

    override fun toString(obj: Boolean?): String {
        return obj?.toString() ?: ""
    }


    override fun errorMessage() = errorMessage(value)

    fun errorMessage(v: Boolean?) = parameter.errorMessage(v)

    override fun toString(): String = "BooleanValue name '${parameter.name}' = ${value}"

}