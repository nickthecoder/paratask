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
package uk.co.nickthecoder.paratask.parameters.compound

import javafx.util.StringConverter

/**
 * Holds a double value with units,
 * For example, store a length, with units of "mm" -> 1.0, "cm" -> 10.0 and "m" -> 1000.0
 * A value of 3 meters is stored using : amount = 3, and a scale = 1000.0. Value = 3000.0.
 */
data class ScaledDouble(
        var amount: Double,
        var scale: Double = 1.0,
        val scales: Map<Double, String>) {

    var value: Double
        get() = amount * scale
        set(v) {
            amount = v / scale
        }

    override fun toString() = "$amount ${scales[scale]}"

    companion object {

        fun converter(scales: Map<Double, String>) = object : StringConverter<ScaledDouble>() {
            override fun fromString(string: String?): ScaledDouble? {
                if (string == null || string.isEmpty()) {
                    return ScaledDouble(0.0, 0.0, emptyMap())
                }
                val star = string.indexOf('*')
                if (star < 0) {
                    return ScaledDouble(string.toDouble(), 1.0, scales)
                } else {
                    return ScaledDouble(string.substring(0, star - 1).toDouble(), string.substring(star + 1).toDouble(), scales)
                }
            }

            override fun toString(obj: ScaledDouble?): String {
                if (obj == null) {
                    return ""
                }

                return "${obj.amount}*${obj.scale}"
            }
        }
    }
}
