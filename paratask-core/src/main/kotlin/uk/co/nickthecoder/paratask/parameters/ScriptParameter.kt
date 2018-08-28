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
import uk.co.nickthecoder.paratask.gui.ScriptVariables
import uk.co.nickthecoder.paratask.parameters.fields.ScriptField
import uk.co.nickthecoder.paratask.util.uncamel

class ScriptParameter(
        name: String,
        val scriptVariables: ScriptVariables,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: String = "",
        required: Boolean = true,
        val columns: Int = 30,
        val rows: Int = 1)

    : AbstractValueParameter<String>(
        name = name,
        label = label,
        description = description,
        hint = hint,
        value = value,
        required = required,
        isBoxed = true) {

    override val converter = object : StringConverter<String>() {
        override fun fromString(str: String): String? = str
        override fun toString(obj: String?): String = obj ?: ""
    }

    override fun isStretchy(): Boolean = true

    override fun errorMessage(v: String?): String? {
        if (isProgrammingMode()) return null
        if (required && (v == null || v.isEmpty())) {
            return "Required"
        }
        return null
    }

    override fun createField(): ScriptField = ScriptField(this).build() as ScriptField

    override fun toString() = "Script" + super.toString()

    override fun copy() = ScriptParameter(name = name, scriptVariables = scriptVariables,
            label = label, description = description, hint = hint, value = value,
            required = required, columns = columns, rows = rows)
}
