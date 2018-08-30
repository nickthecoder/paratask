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
import uk.co.nickthecoder.paratask.util.uncamel

class AlphaColorParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Color = Color.WHITE)

    : CompoundParameter<Color>(name = name,
        label = label,
        description = description) {


    val rgbP = ColorParameter(name + "_rgb", label = "", value = value)
    val alphaP = DoubleParameter(name + "_alpha", label = "Alpha", value = value.opacity, minValue = 0.0, maxValue = 1.0)
            .asSlider(DoubleParameter.SliderInfo(blockIncrement = 0.1, majorTickUnit = 0.5, minorTickCount = 5, snapToTicks = false))

    override var value: Color
        get() {
            val rgb = rgbP.value
            return Color(rgb.red, rgb.green, rgb.blue, alphaP.value ?: 1.0)
        }
        set(v) {
            rgbP.value = v
            alphaP.value = v.opacity
        }


    override val converter = object : StringConverter<Color>() {
        override fun fromString(str: String): Color {
            val trimmed = str.trim()

            try {
                val rgb = trimmed.substring(0, 7)
                val alpha = trimmed.substring(7, 9)
                return Color.web(rgb, Integer.parseInt(alpha, 16) / 255.0)
            } catch (e: Exception) {
                throw ParameterException(this@AlphaColorParameter, "Not a Color")
            }
        }

        override fun toString(obj: Color): String {
            val r = Math.round(obj.red * 255.0).toInt();
            val g = Math.round(obj.green * 255.0).toInt()
            val b = Math.round(obj.blue * 255.0).toInt()
            val a = Math.round(obj.opacity * 255.0).toInt()
            return String.format("#%02x%02x%02x%02x", r, g, b, a)
        }

    }

    init {
        addParameters(rgbP, alphaP)
        asHorizontal(labelPosition = LabelPosition.TOP)
    }

    override fun toString() = "AlphaColor" + super.toString()

    override fun copy() = AlphaColorParameter(name = name, label = label, description = description, value = value)

}
