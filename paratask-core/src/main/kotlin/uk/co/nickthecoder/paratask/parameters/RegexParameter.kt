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
import uk.co.nickthecoder.paratask.parameters.fields.RegexField
import uk.co.nickthecoder.paratask.util.uncamel
import java.util.regex.Pattern

class RegexParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: String = "",
        required: Boolean = true,
        val columns: Int = 30,
        val style: String? = null,
        val stretchy: Boolean = true,
        hidden: Boolean = false,
        enabled: Boolean = true)

    : AbstractValueParameter<String>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required,
        hidden = hidden,
        enabled = enabled) {

    override val converter = object : StringConverter<String>() {
        override fun fromString(str: String): String? = str
        override fun toString(obj: String?): String = obj ?: ""
    }

    override fun isStretchy(): Boolean = stretchy

    fun regex(): Regex? {
        if (value.isEmpty()) {
            return null
        } else {
            return Regex(value)
        }
    }

    fun pattern(): Pattern? {
        if (value.isEmpty()) {
            return null
        } else {
            return Pattern.compile(value)
        }
    }

    override fun errorMessage(v: String?): String? {
        if (isProgrammingMode()) return null
        if (required && (v == null || v.isEmpty())) {
            return "Required"
        }
        if (v != null && v.isNotEmpty()) {
            try {
                Regex(v)
            } catch (e: Exception) {
                return "Not a valid regex pattern"
            }
        }
        return null
    }

    override fun createField(): RegexField = RegexField(this).build() as RegexField

    override fun toString() = "Regex" + super.toString()

    override fun copy() = RegexParameter(name = name, label = label, description = description, value = value,
            required = required, columns = columns, style = style, stretchy = stretchy)
}
