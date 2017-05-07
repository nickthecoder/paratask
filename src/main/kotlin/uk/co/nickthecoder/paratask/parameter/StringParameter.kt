package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.StringField
import uk.co.nickthecoder.paratask.util.uncamel
import javafx.util.StringConverter

class StringParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: String = "",
        required: Boolean = true,
        val columns: Int = 30,
        val stretchy: Boolean = true)

    : AbstractValueParameter<String>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<String>() {
        override fun fromString(str: String): String? = str
        override fun toString(obj: String?): String = obj ?: ""
    }

    override fun isStretchy(): Boolean = stretchy

    override fun errorMessage(v: String?): String? {
        if (required && (v == null || v.length == 0)) {
            return "Required"
        }
        return null
    }

    override fun createField(): StringField = StringField(this)

    override fun toString() = "String" + super.toString()


    companion object {
        fun factory(
                required: Boolean = true
        )
                = StringParameter(
                name = "inner",
                label = "",
                required = required
        )
    }
}