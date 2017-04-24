package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.StringField

class StringParameter(
        name: String,
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<StringValue>(name = name, required = required, columns = columns) {

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(values: Values): String? = errorMessage( valueFrom(values).value )

    fun errorMessage(v: String): String? {
        if (required && v.length == 0) {
            return "Required"
        }
        return null
    }

    override fun createField(values: Values): StringField {
        val value = values.get(name) as StringValue
        return StringField(this, value)
    }

    override fun createValue() = StringValue(this)

    fun valueFrom(values: Values) = values.get(name) as StringValue

}