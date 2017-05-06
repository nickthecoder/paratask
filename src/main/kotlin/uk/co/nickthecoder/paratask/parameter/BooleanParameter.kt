package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.field.BooleanField
import uk.co.nickthecoder.paratask.gui.field.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel
import javafx.util.StringConverter

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Boolean? = null,
        required: Boolean = true,
        val labelOnLeft: Boolean = true)

    : NullableValueParameter<Boolean>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Boolean?>() {
        override fun fromString(str: String): Boolean? {
            val trimmed = str.trim()

            return when (trimmed) {
                "" -> null
                "true" -> true
                "false" -> false
                else -> throw ParameterException(this@BooleanParameter, "Expected 'true' or 'false'")
            }
        }

        override fun toString(obj: Boolean?): String {
            return obj?.toString() ?: ""
        }
    }

    override fun isStretchy() = false

    override fun createField(): LabelledField = BooleanField(this)

    override fun toString(): String = "Boolean" + super.toString()
}