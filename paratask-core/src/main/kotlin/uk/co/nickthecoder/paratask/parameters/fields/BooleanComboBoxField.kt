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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType

/**
 * Renders a BooleanParameter as a ComboBox rather than the usual checkbox
 */
class BooleanComboBoxField(val booleanParameter: BooleanParameter)
    : ParameterField(booleanParameter) {

    private var dirty = false

    val comboBox = ComboBox<String>()

    val bodgeProperty = object : SimpleObjectProperty <String?>("") {
        override fun get(): String = getLabelForValue(booleanParameter.value)
        override fun set(value: String?) {
            booleanParameter.value = getValueForLabel(value)
        }
    }

    fun getLabelForValue(value: Boolean?) = booleanParameter.comboBoxLabels!![value] ?: ""

    fun getValueForLabel(label: String?): Boolean? {
        for (entry in booleanParameter.comboBoxLabels!!) {
            if (label == entry.value) {
                return entry.key
            }
        }
        return null
    }

    override fun createControl(): ComboBox<*> {

        comboBox.valueProperty().bindBidirectional(bodgeProperty)

        updateChoices()

        return comboBox
    }

    private fun updateChoices() {
        comboBox.items.clear()
        booleanParameter.comboBoxLabels!!.values.forEach {
            comboBox.items.add(it)
        }
        comboBox.value = getLabelForValue(booleanParameter.value)

    }

    override fun isDirty(): Boolean = dirty

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        if (event.type == ParameterEventType.VALUE) {
            comboBox.value = getLabelForValue(booleanParameter.value)
        }
    }

}
