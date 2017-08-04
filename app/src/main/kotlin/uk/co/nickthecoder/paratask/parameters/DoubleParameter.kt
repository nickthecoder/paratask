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

package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.DoubleField
import uk.co.nickthecoder.paratask.parameters.fields.IntField
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

open class DoubleParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Double? = null,
        required: Boolean = true,
        var minValue: Double = 0.0,
        var maxValue: Double = Double.MAX_VALUE)

    : AbstractValueParameter<Double?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Double?>() {
        override fun fromString(str: String): Double? {
            val trimmed = str.trim()

            if (trimmed.isEmpty()) {
                return null
            }
            try {
                return trimmed.toDouble()
            } catch (e: Exception) {
                throw ParameterException(this@DoubleParameter, "Not a number")
            }
        }

        override fun toString(obj: Double?): String {
            return obj?.toString() ?: ""
        }
    }

    override fun errorMessage(v: Double?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (v < minValue) {
            return "Cannot be less than ${minValue}"
        } else if (v > maxValue) {
            return "Cannot be more than ${maxValue}"
        }
        return null
    }

    override fun isStretchy() = false

    override fun createField(): LabelledField = DoubleField(this, DoubleParameterAdaptor(this))

    override fun toString(): String = "Double" + super.toString()

    override fun copy() = DoubleParameter(name = name, label = label, description = description, value = value, required = required, minValue = minValue, maxValue = maxValue)
}