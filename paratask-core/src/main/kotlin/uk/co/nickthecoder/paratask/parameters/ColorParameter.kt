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

import javafx.scene.paint.Color
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.ColorField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * A Color, without an alpha channel.
 */
class ColorParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: Color = Color.WHITE)

    : AbstractValueParameter<Color>(
        name = name,
        label = label,
        description = description,
        hint = hint,
        value = value) {

    override val converter = object : StringConverter<Color>() {
        override fun fromString(str: String): Color {
            val trimmed = str.trim()

            try {
                return Color.web(trimmed, 1.0)
            } catch (e: Exception) {
                throw ParameterException(this@ColorParameter, "Not a Color")
            }
        }

        override fun toString(obj: Color): String {
            val r = Math.round(obj.red * 255.0).toInt();
            val g = Math.round(obj.green * 255.0).toInt()
            val b = Math.round(obj.blue * 255.0).toInt()
            return String.format("#%02x%02x%02x", r, g, b)
        }

    }

    override fun errorMessage(v: Color?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return "Required"
        }

        return null
    }

    override fun isStretchy() = false

    override fun createField(): ParameterField = ColorField(this).build()

    override fun toString(): String = "Color" + super.toString()

    override fun copy() = ColorParameter(
            name = name, label = label, description = description, hint = hint, value = value)

}
