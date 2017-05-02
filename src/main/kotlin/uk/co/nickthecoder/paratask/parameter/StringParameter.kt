package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.StringField
import uk.co.nickthecoder.paratask.util.uncamel

class StringParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: String = "",
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<String>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required,
        columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(v: String?): String? {
        if (required && (v == null || v.length == 0)) {
            return "Required"
        }
        return null
    }

    override fun createField(values: Values): StringField = StringField(this, parameterValue(values))

    override fun createValue() = StringValue(this, value)

    override fun parameterValue(values: Values) = super.parameterValue(values) as StringValue

    override fun toString() = "String" + super.toString()

}