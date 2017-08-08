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

import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.parameters.BooleanParameter

class BooleanField(val booleanParameter: BooleanParameter)
    : LabelledField(booleanParameter, label = if (booleanParameter.labelOnLeft) booleanParameter.label else "") {

    override fun createControl(): CheckBox {

        val checkBox = CheckBox(if (booleanParameter.labelOnLeft) "" else parameter.label)
        checkBox.isAllowIndeterminate = !booleanParameter.required
        if (booleanParameter.value == null) {
            checkBox.isIndeterminate = true
        } else {
            checkBox.isSelected = booleanParameter.value == true
        }
        checkBox.selectedProperty().bindBidirectional(booleanParameter.valueProperty)

        label.addEventHandler(MouseEvent.MOUSE_CLICKED, {
            checkBox.requestFocus()
            booleanParameter.value = when (booleanParameter.value) {
                null -> true
                true -> false
                false -> if (booleanParameter.required) true else null
            }
            checkBox.isIndeterminate = booleanParameter.value == null
        })

        return checkBox
    }

}