package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.LabelledField
import uk.co.nickthecoder.paratask.gui.IntField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        val value: Int? = null,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE)

) : ValueParameter<IntValue>(name = name, label = label, required = required) {

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

    override fun createField(values: Values): LabelledField = IntField(this, values)

    override fun createValue() = IntValue(this, value)

    fun valueFrom(values: Values) = values.get(name) as IntValue

    override fun copyValue(source: Values): IntValue {
        val copy = IntValue(this, valueFrom(source).value)
        return copy
    }
}