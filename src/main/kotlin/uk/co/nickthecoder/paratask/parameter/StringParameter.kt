package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.StringField
import uk.co.nickthecoder.paratask.util.uncamel

class StringParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val value: String = "",
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<String>(
        name = name,
        label = label,
        description = description,
        required = required,
        columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(v: String?): String? {
        if (required && (v == null || v.length == 0)) {
            return "Required"
        }
        return null
    }

    override fun createField(values: Values): StringField = StringField(this, values)

    override fun createValue() = StringValue(this, value)

    override fun copyValue(source: Values): StringValue {
        val copy = StringValue(this, value(source) ?: "")
        return copy
    }

    override fun getValue(values: Values) = super.getValue(values) as StringValue

    override fun toString() = "String" + super.toString()

}