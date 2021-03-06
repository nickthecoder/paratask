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
import uk.co.nickthecoder.paratask.parameters.fields.IntField
import uk.co.nickthecoder.paratask.parameters.fields.IntSliderField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: Int? = null,
        required: Boolean = true,
        val minValue: Int = Int.MIN_VALUE,
        val maxValue: Int = Int.MAX_VALUE,
        val columnCount: Int = 6)

    : AbstractValueParameter<Int?>(
        name = name,
        label = label,
        description = description,
        hint = hint,
        value = value,
        required = required) {


    var fieldFactory: (IntParameter) -> ParameterField = {
        IntField(this).build()
    }

    fun asSlider(sliderInfo: SliderInfo = SliderInfo()): IntParameter {
        fieldFactory = {
            IntSliderField(this, sliderInfo).build()
        }
        return this
    }

    override val converter = object : StringConverter<Int?>() {
        override fun fromString(str: String): Int? {
            val trimmed = str.trim()

            if (trimmed.isEmpty()) {
                return null
            }
            try {
                return Integer.parseInt(trimmed)
            } catch (e: Exception) {
                throw ParameterException(this@IntParameter, "Not an integer")
            }
        }

        override fun toString(obj: Int?): String {
            return obj?.toString() ?: ""
        }

    }

    override fun errorMessage(v: Int?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (v > maxValue) {
            return "Cannot be more than $maxValue"
        } else if (v < minValue) {
            return "Cannot be less than $minValue"
        }

        return null
    }

    override fun isStretchy() = false

    override fun createField(): ParameterField = fieldFactory(this)

    override fun toString(): String = "Int" + super.toString()

    override fun copy() = IntParameter(name = name, label = label, description = description, hint = hint, value = value,
            required = required, minValue = minValue, maxValue = maxValue)

    data class SliderInfo(
            val blockIncrement: Int = 1,
            val majorTickUnit: Int = 1,
            val minorTickCount: Int = 0,
            val horizontal: Boolean = true,
            val snapToTicks: Boolean = true,
            val showValue: Boolean = false
    )
}
