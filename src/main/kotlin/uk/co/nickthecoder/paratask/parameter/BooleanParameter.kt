package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.BooleanField
import uk.co.nickthecoder.paratask.gui.field.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Boolean? = null,
        required: Boolean = true,
        val labelOnLeft: Boolean = true)

    : ValueParameter<Boolean?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override fun isStretchy() = false

    override fun createField(values: Values): LabelledField = BooleanField(this, parameterValue(values))

    override fun createValue() = BooleanValue(this, value)

    override fun parameterValue(values: Values) = super.parameterValue(values) as BooleanValue

    override fun toString(): String = "Boolean" + super.toString()
}