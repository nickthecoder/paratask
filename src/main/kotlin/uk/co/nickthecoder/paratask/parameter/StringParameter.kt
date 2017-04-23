package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.StringField

class StringParameter(
        name: String,
        value: String = "",
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<String>(name = name, required = required, columns = columns) {

    init {
        this.value = value
    }

    override fun setStringValue(s: String) {
        value = s
    }

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(v: String?): String? {
        if (v == null) {
            return "Null values not allowed"
        }
        if (v.length == 0) {
            return super.errorMessage(null)
        }
        return null;
    }

    override fun createField() = StringField(this)
}