package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.LabelledField
import uk.co.nickthecoder.paratask.gui.field.IntField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Int? = null,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE))

    : ValueParameter<Int?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

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

    override fun createField(values: Values): LabelledField = IntField(this, parameterValue(values))

    override fun createValue() = IntValue(this, value)

    override fun parameterValue(values: Values) = super.parameterValue(values) as IntValue

    override fun toString(): String = "Int" + super.toString()
}