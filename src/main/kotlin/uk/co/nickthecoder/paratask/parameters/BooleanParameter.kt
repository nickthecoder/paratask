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
import uk.co.nickthecoder.paratask.parameters.fields.BooleanField
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Boolean? = null,
        required: Boolean = true,
        val oppositeName: String? = null,
        val labelOnLeft: Boolean = true)

    : AbstractValueParameter<Boolean?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Boolean?>() {
        override fun fromString(str: String): Boolean? {
            val trimmed = str.trim()

            return when (trimmed) {
                "" -> null
                "true" -> true
                "false" -> false
                else -> throw ParameterException(this@BooleanParameter, "Expected 'true' or 'false'")
            }
        }

        override fun toString(obj: Boolean?): String {
            return obj?.toString() ?: ""
        }
    }

    override fun isStretchy() = false

    override fun createField(): LabelledField = BooleanField(this)

    override fun toString(): String = "Boolean" + super.toString()

    override fun copy() = BooleanParameter(name = name, label = label, description = description, value = value,
            required = required, oppositeName = oppositeName, labelOnLeft = labelOnLeft)

}