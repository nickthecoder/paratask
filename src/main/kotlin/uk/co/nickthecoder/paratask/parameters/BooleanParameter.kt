package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.BooleanField
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Boolean? = null,
        required: Boolean = true,
        val oppositeName: String? = null,
        val labelOnLeft: Boolean = true)

    : AbstractValueParameter<Boolean?>(
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