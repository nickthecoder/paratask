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
import uk.co.nickthecoder.paratask.parameters.fields.FloatField
import uk.co.nickthecoder.paratask.util.uncamel
import java.text.DecimalFormat

private val doubleFormat = DecimalFormat("0.#######")

open class FloatParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: Float? = null,
        required: Boolean = true,
        val columnCount: Int = 8,
        val minValue: Float = -Float.MAX_VALUE,
        val maxValue: Float = Float.MAX_VALUE)

    : AbstractValueParameter<Float?>(
        name = name,
        label = label,
        description = description,
        hint = hint,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Float?>() {
        override fun fromString(str: String): Float? {
            val trimmed = str.trim()

            if (trimmed.isEmpty()) {
                return null
            }
            try {
                return trimmed.toFloat()
            } catch (e: Exception) {
                throw ParameterException(this@FloatParameter, "Not a number")
            }
        }

        override fun toString(obj: Float?): String {
            if (obj == null) {
                return ""
            }
            val l = obj.toLong()
            if (obj == l.toFloat()) {
                return l.toString()
            } else {
                return doubleFormat.format(obj)
            }
        }
    }

    override fun errorMessage(v: Float?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (v < minValue) {
            return "Cannot be less than $minValue"
        } else if (v > maxValue) {
            return "Cannot be more than $maxValue"
        }
        return null
    }

    override fun isStretchy() = false

    override fun createField(): FloatField =
            FloatField(this).build() as FloatField

    override fun toString(): String = "Double" + super.toString()

    override fun copy() = FloatParameter(name = name, label = label, description = description, hint = hint,
            value = value, required = required, minValue = minValue, maxValue = maxValue, columnCount = columnCount)
}
