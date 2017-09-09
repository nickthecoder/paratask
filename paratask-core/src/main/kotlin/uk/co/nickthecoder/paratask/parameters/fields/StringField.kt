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

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.focusNext

class StringField(val stringParameter: StringParameter)
    : ParameterField(stringParameter) {

    lateinit var textField: TextInputControl


    override fun createControl(): TextInputControl {

        val textField: TextInputControl

        if (stringParameter.rows > 1) {
            textField = TextArea()
            if (stringParameter.columns > 0) {
                textField.prefColumnCount = stringParameter.columns
            }
            textField.prefRowCount = stringParameter.rows
            textField.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyTyped(it) }
        } else {
            textField = TextField()
            if (stringParameter.columns > 0) {
                textField.prefColumnCount = stringParameter.columns
            }
        }
        stringParameter.style?.let { textField.styleClass.add(it) }

        textField.text = stringParameter.value
        textField.textProperty().bindBidirectional(stringParameter.valueProperty)
        textField.textProperty().addListener({ _, _, _: String ->
            val error = stringParameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        this.textField = textField
        return textField
    }

    fun onKeyTyped(event: KeyEvent) {
        if (ShortcutHelper.FOCUS_NEXT.match(event)) {
            textField.focusNext()
            event.consume()
        } else if (ShortcutHelper.INSERT_TAB.match(event)) {
            textField.insertText(textField.caretPosition, "    ")
            event.consume()
        }
    }

}
