/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.paratask.parameters.compound

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel

class IntRangeParameter(
        name: String,
        override val label: String = name.uncamel(),
        val required: Boolean = true,
        val inclusive: Boolean = true, // Used by contains()
        val minValue: Int = Int.MIN_VALUE,
        val maxValue: Int = Int.MAX_VALUE,
        toText: String? = "To",
        description: String = "")

    : GroupParameter(
        name = name,
        label = label,
        description = description) {

    val fromP = IntParameter(name + "_from", label = "From", required = required, minValue = minValue, maxValue = maxValue)
    var from by fromP

    val toP = IntParameter(name + "_to", label = "To", required = required, minValue = minValue, maxValue = maxValue)
    var to by toP


    init {
        addParameters(fromP)
        if (toText != null) {
            addParameters(InformationParameter(name + "_info", information = toText, stretchy = false))
        }
        addParameters(toP)
        asHorizontal(LabelPosition.NONE)
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
