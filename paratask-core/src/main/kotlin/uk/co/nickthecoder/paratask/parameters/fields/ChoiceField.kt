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
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType

// Note. JavaFX cannot handle null values in ComboBox correctly
// See : http://stackoverflow.com/questions/25877323/no-select-item-on-javafx-combobox
// So I've added a "special" value, and made the generic type "ANY?"
// and added a bodgeProperty, which forwards get/sets to the parameter's property

private val FAKE_NULL = "FAKE_NULL"

class ChoiceField<T>(val choiceParameter: ChoiceParameter<T>)
    : ParameterField(choiceParameter) {

    private var dirty = false

    val comboBox = ComboBox<Any?>()

    val converter = object : StringConverter<Any?>() {

        override fun fromString(label: String): Any? {
            return choiceParameter.getValueForLabel(label) ?: FAKE_NULL
        }

        override fun toString(obj: Any?): String {
            @Suppress("UNCHECKED_CAST")
            return choiceParameter.getLabelForValue(if (obj === FAKE_NULL) null else obj as T) ?: ""
        }
    }

    val bodgeProperty = object : SimpleObjectProperty <Any?>(FAKE_NULL) {
        override fun get(): Any? {
            if (choiceParameter.value == null) {
                return FAKE_NULL
            } else {
                return choiceParameter.value!!
            }
        }

        override fun set(value: Any?) {
            if (value == null || value === FAKE_NULL) {
                choiceParameter.value = null

            } else {
                @Suppress("UNCHECKED_CAST")
                choiceParameter.value = value as T?
            }
        }
    }

    override fun createControl(): ComboBox<*> {
        comboBox.converter = converter

        updateChoices()
        comboBox.valueProperty().bindBidirectional(bodgeProperty)

        return comboBox
    }

    private fun updateChoices() {
        val pValue = choiceParameter.value

        comboBox.items.clear()
        choiceParameter.choices().map { it.value ?: FAKE_NULL }.forEach {
            comboBox.items.add(it)
        }
        comboBox.value = pValue
    }

    override fun isDirty(): Boolean = dirty

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        if (event.type == ParameterEventType.STRUCTURAL) {
            updateChoices()
        } else if (event.type == ParameterEventType.VALUE) {
            comboBox.value = choiceParameter.value
        }
        showOrClearError(choiceParameter.errorMessage())

    }

}
