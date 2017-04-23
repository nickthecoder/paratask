package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.IntField

open class IntParameter(
        name: String,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE)

) : ValueParameter<Int?>(name = name, required = required) {

    val converter = IntStringConverter()

    override fun errorMessage(v: Int?): String? {
        if (v != null) {
            if (!range.contains(v)) {
                if (range.start == Int.MIN_VALUE) {
                    return "Cannot be more than ${range.endInclusive}"
                } else if (range.endInclusive == Int.MAX_VALUE) {
                    return "Cannot be less than ${range.start}"
                } else {
                    return "Must be in the range ${range.start}..${range.endInclusive}"
                }
            }
        }
        return super.errorMessage(v)
    }

    override fun isStretchy() = false

    override fun setStringValue(s: String) {
        value = converter.fromString(s)
    }

    override fun getStringValue(): String {
        return converter.toString(value)
    }

    override fun createField(): Field = IntField(this)

    inner public class IntStringConverter() : StringConverter<Int?>() {

        override fun fromString(str: String?): Int? {

            try {
                if (str == null) {
                    check(null)
                    return null
                }
                val trimmed = str.trim()

                if (trimmed.length == 0) {
                    check(null)
                    return null
                }
                val v = Integer.parseInt(trimmed);
                check(v)
                return v
            } catch (e: NumberFormatException) {
                throw ParameterException(this@IntParameter, "Not an integer")
            }
        }

        override fun toString(value: Int?): String {
            if (value == null) {
                return "";
            }

            return value.toString();
        }
    }
}