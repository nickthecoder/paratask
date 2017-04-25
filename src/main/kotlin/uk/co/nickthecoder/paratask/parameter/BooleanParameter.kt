package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.BooleanField
import uk.co.nickthecoder.paratask.gui.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        val value: Boolean? = null,
        required: Boolean = true,
        val labelOnLeft: Boolean = true)

    : ValueParameter<IntValue>(name = name, label = label, required = required) {

    override fun errorMessage(values: Values): String? = errorMessage(valueFrom(values).value)

    fun errorMessage(v: Boolean?): String? {

        if (v == null) {
            if (required) return "Required"
        }
        return null
    }

    override fun isStretchy() = false

    override fun createField(values: Values): LabelledField = BooleanField(this, values)

    override fun createValue() = BooleanValue(this, value)

    fun valueFrom(values: Values) = values.get(name) as BooleanValue

    override fun copyValue(source: Values): BooleanValue {
        val copy = BooleanValue(this, valueFrom(source).value)
        return copy
    }

    override fun toString(): String {
        return "BooleanParameter ${name}"
    }
}