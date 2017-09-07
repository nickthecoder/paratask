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

import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.RegexParameter
import uk.co.nickthecoder.paratask.util.focusNext

class RegexField(val regexParameter: RegexParameter) : LabelledField(regexParameter) {

    override fun createControl(): TextField {
        val textField = TextField()

        if (regexParameter.columns > 0) {
            textField.prefColumnCount = regexParameter.columns
        }
        regexParameter.style?.let { textField.styleClass.add(it) }

        textField.text = regexParameter.value
        textField.textProperty().bindBidirectional(regexParameter.valueProperty)
        textField.textProperty().addListener({ _, _, _: String ->
            val error = regexParameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        return textField
    }

}
