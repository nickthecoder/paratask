package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException

class IntValue(
        override val parameter: IntParameter,
        initialValue: Int? = null)

    : AbstractValue<Int?>(initialValue) {

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

    override fun errorMessage(v: Int?) = parameter.errorMessage(v)

    override fun toString(): String = "Int" + super.toString()

}