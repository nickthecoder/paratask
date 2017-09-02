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

// Note. JavaFX cannot handle null values in Combobox correctly
// See : http://stackoverflow.com/questions/25877323/no-select-item-on-javafx-combobox
// So I've added a "special" value, and made the generic type "ANY?"
// and added a bodgeProperty, which forwards get/sets to the parameter's property

private val FAKE_NULL = "FAKE_NULL"

class ChoiceField<T>(val choiceParameter: ChoiceParameter<T>) : LabelledField(choiceParameter) {

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
        override fun get(): Any? = choiceParameter.value ?: FAKE_NULL
        override fun set(value: Any?) {
            if (value === FAKE_NULL) {
                choiceParameter.value = null

            } else {
                @Suppress("UNCHECKED_CAST")
                choiceParameter.value = value as T?
            }
        }
    }

    override fun createControl(): ComboBox<*> {

        comboBox.converter = converter
        comboBox.valueProperty().bindBidirectional(bodgeProperty)

        updateChoices()

        return comboBox
    }

    private fun updateChoices() {
        comboBox.items.clear()
        for (value: T? in choiceParameter.choiceValues()) {
            comboBox.items.add(value ?: FAKE_NULL)
        }
        comboBox.value = choiceParameter.value ?: FAKE_NULL

    }

    override fun isDirty(): Boolean = dirty

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        if (event.type == ParameterEventType.STRUCTURAL) {
            updateChoices()
        } else if (event.type == ParameterEventType.VALUE) {
            comboBox.value = choiceParameter.value
        }


    }

}
