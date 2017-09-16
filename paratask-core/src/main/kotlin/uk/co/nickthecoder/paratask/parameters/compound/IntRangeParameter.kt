package uk.co.nickthecoder.paratask.parameters.compound

import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.util.uncamel

class IntRangeParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        val inclusive: Boolean = true, // Used by contains()
        val minValue: Int = Int.MIN_VALUE,
        val maxValue: Int = Int.MAX_VALUE,
        description: String = "")

    : AbstractGroupParameter(
        name = name,
        label = label,
        description = description) {

    val fromP = IntParameter(name + "_from", label = "", required = required, minValue = minValue, maxValue = maxValue)
    var from by fromP

    val toP = IntParameter(name + "_to", label = "… ", required = required, minValue = minValue, maxValue = maxValue)
    var to by toP


    init {
        addParameters(fromP, toP)
        asHorizontal(false)
    }

    override fun saveChildren(): Boolean = true

    fun contains(value: Int): Boolean {
        if (from ?: Int.MAX_VALUE > value) {
            return false
        }
        if (inclusive) {
            if (to ?: Int.MIN_VALUE <= value) {
                return false
            }
        } else {
            if (to ?: Int.MIN_VALUE < value) {
                return false
            }
        }
        return true
    }

    override fun errorMessage(): String? {
        if (isProgrammingMode()) return null

        if (fromP.value != null && toP.value != null && fromP.value!! > toP.value!!) {
            return "'from' cannot be larger than 'to'"
        }
        return null
    }

    override fun copy(): IntRangeParameter {
        val copy = IntRangeParameter(name = name,
                label = label,
                description = description,
                required = required,
                inclusive = inclusive,
                minValue = minValue,
                maxValue = maxValue)
        copyAbstractAttributes(copy)
        return copy
    }

}
