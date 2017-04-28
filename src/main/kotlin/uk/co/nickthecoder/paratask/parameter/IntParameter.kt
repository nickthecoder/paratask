package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.LabelledField
import uk.co.nickthecoder.paratask.gui.IntField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        val value: Int? = null,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE))

    : ValueParameter<Int?>(
        name = name,
        label = label,
        description = description,
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

    override fun isStretchy() = false

    override fun createField(values: Values): LabelledField = IntField(this, values)

    override fun createValue() = IntValue(this, value)

    fun multiple(): MultipleParameter<Int?> =
            MultipleParameter(this, name = name, label = label, description = description, value = value)

    override fun copyValue(source: Values): IntValue {
        val v: Int? = value(source)
        val copy = IntValue(this, v)
        return copy
    }

    override fun getValue(values: Values) = super.getValue(values) as IntValue


    override fun toString(): String = "Int" + super.toString()
}