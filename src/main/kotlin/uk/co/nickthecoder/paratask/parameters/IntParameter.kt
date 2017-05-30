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
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

open class IntParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Int? = null,
        required: Boolean = true,
        var range: IntRange = IntRange(Int.MIN_VALUE, Int.MAX_VALUE))

    : AbstractValueParameter<Int?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

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

    override fun createField(): LabelledField = IntField(this)

    override fun toString(): String = "Int" + super.toString()

    override fun copy() = IntParameter(name = name, label = label, description = description, value = value, required = required)
}
