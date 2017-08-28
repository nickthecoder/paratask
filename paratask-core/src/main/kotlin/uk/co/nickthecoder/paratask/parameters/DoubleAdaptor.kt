/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter

/**
 * Allows DoubleField to be used by DoubleParameter and ScaledDoubleParameter
 */
interface DoubleAdaptor {
    var value: Double?

    val converter: StringConverter<Double?>

    fun errorMessage(v: Double?): String?

    val minValue: Double

    val maxValue: Double

    val valueProperty: SimpleObjectProperty<Double?>
}

class DoubleParameterAdaptor(val doubleParameter: DoubleParameter) : DoubleAdaptor {

    override var value: Double?
        get() = doubleParameter.value
        set(v) {
            doubleParameter.value = v
        }

    override val converter: StringConverter<Double?>
        get() = doubleParameter.converter

    override fun errorMessage(v: Double?) = doubleParameter.errorMessage(v)

    override val minValue: Double = doubleParameter.minValue

    override val maxValue: Double = doubleParameter.maxValue

    override val valueProperty: SimpleObjectProperty<Double?> = doubleParameter.valueProperty

}

class ScaledDoubleParameterAdaptor(val scaledDoubleParameter: ScaledDoubleParameter) : DoubleAdaptor {

    override var value: Double?
        get() = scaledDoubleParameter.value.value
        set(v) {
            scaledDoubleParameter.value.value = v!!
        }

    override val converter = object : StringConverter<Double?>() {
        override fun fromString(string: String): Double? = string.toDouble()
        override fun toString(obj: Double?): String = obj.toString()
    }

    override fun errorMessage(v: Double?) = null

    override val minValue: Double = Double.MIN_VALUE

    override val maxValue: Double = Double.MAX_VALUE


    override var valueProperty = object : SimpleObjectProperty<Double?>() {
        override fun set(v: Double?) {
            val changed = v != get()
            if (changed) {
                super.set(v)
                scaledDoubleParameter.value.value = v!!
                scaledDoubleParameter.parameterListeners.fireValueChanged(scaledDoubleParameter)
            }
        }
        override fun get() = scaledDoubleParameter.value.value
    }
}
