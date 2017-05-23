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
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.util.focusNext

class StringField(override val parameter: StringParameter) : LabelledField(parameter) {

    lateinit var textField: TextInputControl

    private fun createControl(): Node {
        val textField: TextInputControl

        if (parameter.rows > 1) {
            textField = TextArea()
            if (parameter.columns > 0) {
                textField.prefColumnCount = parameter.columns
            }
            textField.prefRowCount = parameter.rows
            textField.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyTyped(it) }
        } else {
            textField = TextField()
            if (parameter.columns > 0) {
                textField.prefColumnCount = parameter.columns
            }
        }
        parameter.style?.let { textField.styleClass.add(it) }

        textField.text = parameter.value
        textField.textProperty().bindBidirectional(parameter.valueProperty)
        textField.textProperty().addListener({ _, _, _: String ->
            val error = parameter.errorMessage()
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
        if ( Actions.FOCUS_NEXT.match(event)) {
            textField.focusNext()
            event.consume()
        } else if ( Actions.INSERT_TAB.match(event) ) {
            textField.insertText(textField.caretPosition, "    ")
            event.consume()
        }
    }

    init {
        control = createControl()
    }
}
