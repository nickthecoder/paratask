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

import javafx.scene.Node
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameters.BooleanParameter

class BooleanField(override val parameter: BooleanParameter)
    : LabelledField(parameter, label = if (parameter.labelOnLeft) parameter.label else "") {

    private fun createControl(): Node {
        val checkBox = CheckBox(if (parameter.labelOnLeft) "" else parameter.label)
        checkBox.isAllowIndeterminate = !parameter.required
        if (parameter.value == null) {
            checkBox.isIndeterminate = true
        } else {
            checkBox.isSelected = parameter.value == true
        }
        checkBox.selectedProperty().bindBidirectional(parameter.valueProperty)

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            parameter.value = when (parameter.value) {
                null -> true
                true -> false
                false -> if (parameter.required) true else null
            }
            checkBox.isIndeterminate = parameter.value == null
        })

        return checkBox
    }

    init {
        control = createControl()
    }

}