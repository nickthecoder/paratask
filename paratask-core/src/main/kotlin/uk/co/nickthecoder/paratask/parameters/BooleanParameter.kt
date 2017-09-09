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
import uk.co.nickthecoder.paratask.parameters.fields.BooleanComboBoxField
import uk.co.nickthecoder.paratask.parameters.fields.BooleanField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

class BooleanParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Boolean? = null,
        required: Boolean = true,
        val oppositeName: String? = null,
        val labelOnLeft: Boolean = true,
        hidden: Boolean = false,
        enabled: Boolean = true)

    : AbstractValueParameter<Boolean?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required,
        hidden = hidden,
        enabled = enabled) {

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

    internal var comboBoxLabels: Map<Boolean?, String>? = null

    override fun isStretchy() = false

    override fun createField(): ParameterField {
        if (comboBoxLabels != null) {
            return BooleanComboBoxField(this).build()
        } else {
            return BooleanField(this).build()
        }
    }

    override fun coerce(v: Any?) {
        if (v is Boolean?) {
            value = v
            return
        }
        super.coerce(v)
    }

    fun asComboBox(map: Map<Boolean?, String> = if (required) NOT_NULLABLE_MAP else NULLABLE_MAP) {
        comboBoxLabels = map
    }

    fun asComboBox(trueLabel: String, falseLabel: String) {
        asComboBox(mapOf(Pair(true, trueLabel), Pair(false, falseLabel)))
    }

    fun asComboBox(trueLabel: String, falseLabel: String, nullLabel: String) {
        asComboBox(mapOf(Pair(true, trueLabel), Pair(false, falseLabel), Pair(null, nullLabel)))
    }

    override fun toString(): String = "Boolean" + super.toString()

    override fun copy() = BooleanParameter(name = name, label = label, description = description, value = value,
            required = required, oppositeName = oppositeName, labelOnLeft = labelOnLeft)

    companion object {

        val NULLABLE_MAP = mutableMapOf<Boolean?, String>(Pair(true, "True"), Pair(false, "False"), Pair(null, "Null"))

        val NOT_NULLABLE_MAP = mutableMapOf<Boolean?, String>(Pair(true, "True"), Pair(false, "False"))

    }
}