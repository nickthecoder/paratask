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

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.uncamel

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
        description = description), ValueParameter<ScaledDouble?> {

    val amountP = DoubleParameter(name = "_amount", label = "", required = required)
    var amount by amountP

    val scaleP = ChoiceParameter(name = "_scale", value = 1.0, label = "")
    var scale by scaleP

    val units = value.scales

    override fun saveChildren(): Boolean = false

    override val converter: StringConverter<ScaledDouble?> = ScaledDouble.converter(value.scales)

    override var value: ScaledDouble?
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

    init {
        this.value = value
        horizontalLayout(false)
        units.forEach { scale, lab ->
            scaleP.addChoice(scale.toString(), scale, lab)
        }
        addParameters(amountP, scaleP)
    }

    override val expressionProperty = SimpleStringProperty()

    override fun errorMessage(v: ScaledDouble?): String? {

        if (isProgrammingMode()) return null
        if (v == null) {
            if (amountP.required) {
                return "Required"
            }
            return null
        }

        if (v.value < minValue) {
            return "Cannot be less than ${minValue / (scale ?: 1.0)}"
        } else if (v.amount > maxValue) {
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
