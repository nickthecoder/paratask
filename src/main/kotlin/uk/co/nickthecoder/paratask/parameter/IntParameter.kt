package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.field.IntField
import uk.co.nickthecoder.paratask.gui.field.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Int? = null,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE))

    : NullableValueParameter<Int?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Int?>() {
        override fun fromString(str: String): Int? {
            val trimmed = str.trim()

            if (trimmed.length == 0) {
                return null
            }
            try {
                return Integer.parseInt(trimmed);
            } catch (e: Exception) {
                throw ParameterException(this@IntParameter, "Not an integer")
            }
        }

        override fun toString(obj: Int?): String {
            return obj?.toString() ?: ""
        }

    }

    override fun errorMessage(v: Int?): String? {

        if (v == null) {
            return super.errorMessage(v)
        }

        if (!range.contains(v)) {
            if (range.start == Int.MIN_VALUE) {
                return "Cannot be more than ${range.endInclusive}"
            } else if (range.endInclusive == Int.MAX_VALUE) {
                return "Cannot be less than ${range.start}"
            } else {
                return "Must be in the range ${range.start}..${range.endInclusive}"
            }
        }

        return null
    }

    fun min(minimum: Int): IntParameter {
        range = minimum..range.endInclusive
        return this
    }

    fun max(maximum: Int): IntParameter {
        range = range.start..maximum
        return this
    }

    override fun isStretchy() = false

    override fun createField(): LabelledField = IntField(this)

    override fun toString(): String = "Int" + super.toString()
}