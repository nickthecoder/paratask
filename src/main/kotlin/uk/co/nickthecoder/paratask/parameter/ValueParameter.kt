package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import uk.co.nickthecoder.paratask.ParameterException

/**
 * The base class for all Parameters, which can hold a value.
 */
abstract class ValueParameter<T>(
        name: String,
        label: String,
        description: String,
        var required: Boolean = false)

    : AbstractParameter(name, label = label, description = description) {

    open fun parameterValue(values: Values) = values.get(name)

    fun value(values: Values): T? = parameterValue(values)?.value as T?

    fun set(values: Values, v: T) {
        val value: Value<T>? = parameterValue(values) as Value<T>
        value!!.value = v
    }

    override fun errorMessage(values: Values): String? = errorMessage(value(values))

    open fun errorMessage(v: T?): String? = if (v == null && required) "Required" else null
}
