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
package uk.co.nickthecoder.paratask.parameters.compound

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.uncamel
import kotlin.reflect.KProperty

/**
 * See [ScaledDouble]
 */
class ScaledDoubleParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        required: Boolean = true,
        value: ScaledDouble,
        val minValue: Double = 0.0,
        val maxValue: Double = Double.MAX_VALUE)

    : AbstractGroupParameter(
        name = name,
        label = label,
        description = description) {

    val amountP = DoubleParameter(name = "_amount", label = "", required = required)
    var amount by amountP

    val scaleP = ChoiceParameter<Double>(name = "_scale", value = 1.0, label = "")
    var scale by scaleP

    val units = value.scales

    var value: ScaledDouble?
        get() {
            if (amount == null || scale == null) {
                return null
            }
            return ScaledDouble(amount!!, scale!!, units)
        }
        set(v) {
            if (v == null) {
                amount = null
                scale = null
            } else {
                amount = v.amount
                scale = v.scale
            }
        }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): ScaledDouble? {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: ScaledDouble?) {
        this.value = value
    }

    init {
        this.value = value
        boxLayout(false)
        units.forEach { scale, label ->
            scaleP.addChoice(scale.toString(), scale, label)
        }
        addParameters(amountP, scaleP)
    }

    override fun errorMessage(): String? {
        if (isProgrammingMode()) return null

        // Let the inner parameters handle errors for null values
        if (amount == null || scale == null) {
            return null
        }

        val v = (amount ?: 0.0) * (scale ?: 0.0)

        if (v < minValue) {
            return "Cannot be less than ${minValue / (scale ?: 1.0)}"
        } else if (v > maxValue) {
            return "Cannot be more than ${maxValue / (scale ?: 1.0)}"
        }
        return null
    }

    override fun copy(): ScaledDoubleParameter {
        val copy = ScaledDoubleParameter(name, label, description,
                value = ScaledDouble(0.0, 0.0, units),
                required = scaleP.required,
                minValue = minValue,
                maxValue = maxValue)
        copy.value = value
        copyAbstractAttributes(copy)
        return copy
    }

}
