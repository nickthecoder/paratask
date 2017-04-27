package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException

class BooleanValue(
        override val parameter: BooleanParameter,
        initialValue: Boolean? = null)

    : AbstractValue<Boolean?>(initialValue) {

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

    override fun errorMessage(v: Boolean?) = parameter.errorMessage(v)

    override fun toString(): String = "Boolean" + super.toString()
}