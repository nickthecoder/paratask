package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.StringField
import uk.co.nickthecoder.paratask.util.uncamel

class StringParameter(
        name: String,
        label: String = name.uncamel(),
        val value: String = "",
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<StringValue>(name = name, label = label, required = required, columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(values: Values): String? = errorMessage(parameterValue(values).value)

    fun errorMessage(v: String): String? {
        if (required && v.length == 0) {
            return "Required"
        }
        return null
    }

    override fun createField(values: Values): StringField = StringField(this, values)

    override fun createValue() = StringValue(this, value)

    fun parameterValue(values: Values) = values.get(name) as StringValue

    fun value(values: Values) = parameterValue(values).value

    override fun copyValue(source: Values): StringValue {
        val copy = StringValue(this, parameterValue(source).value)
        return copy
    }

    override fun toString(): String {
        return "StringParameter ${name}"
    }

}