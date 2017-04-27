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

    : ValueParameter<Boolean?>(
        name = name,
        label = label,
        description = description,
        required = required) {

    override fun isStretchy() = false

    override fun createField(values: Values): LabelledField = BooleanField(this, values)

    override fun createValue() = BooleanValue(this, value)

    override fun copyValue(source: Values): BooleanValue {
        val copy = BooleanValue(this, value(source))
        return copy
    }

    override fun getValue(values: Values) = super.getValue(values) as BooleanValue

    override fun toString(): String = "Boolean" + super.toString()
}