package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.Field
import uk.co.nickthecoder.paratask.gui.IntField

open class IntParameter(
        name: String,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE)

) : ValueParameter<IntValue>(name = name, required = required) {

    override fun errorMessage(values: Values): String? = errorMessage(valueFrom(values).value)

    fun errorMessage(v: Int?): String? {

        if (v == null) {
            if (required) return "Required"

        } else {
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
        return null
    }

    override fun isStretchy() = false

    override fun createField(values: Values): Field = IntField(this, values)

    override fun createValue() = IntValue(this)

    fun valueFrom(values: Values) = values.get(name) as IntValue

}