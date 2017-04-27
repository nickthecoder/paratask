package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.BooleanField
import uk.co.nickthecoder.paratask.gui.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val value: Boolean? = null,
        required: Boolean = true,
        val labelOnLeft: Boolean = true)

    : ValueParameter<IntValue>(name = name, label = label, description = description, required = required) {

    override fun errorMessage(values: Values): String? = errorMessage(value(values))

    fun errorMessage(v: Boolean?): String? {

        if (v == null) {
            if (required) return "Required"
        }
        return null
    }

    override fun isStretchy() = false

    override fun createField(values: Values): LabelledField = BooleanField(this, values)

    override fun createValue() = BooleanValue(this, value)

    fun parameterValue(values: Values) = values.get(name) as BooleanValue

    fun value(values: Values) = parameterValue(values).value

    override fun copyValue(source: Values): BooleanValue {
        val copy = BooleanValue(this, value(source))
        return copy
    }

    override fun toString(): String {
        return "BooleanParameter ${name}"
    }
}