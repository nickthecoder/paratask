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

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.gui.VariablePrompter
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.ScriptParameter
import uk.co.nickthecoder.paratask.util.focusNext

class ScriptField(val scriptParameter: ScriptParameter)
    : ParameterField(scriptParameter) {

    lateinit var textField: TextInputControl

    var promptButton = Button("…")

    override fun createControl(): Node {

        val textField: TextInputControl

        if (scriptParameter.rows > 1) {
            textField = TextArea()
            if (scriptParameter.columns > 0) {
                textField.prefColumnCount = scriptParameter.columns
            }
            textField.prefRowCount = scriptParameter.rows
            textField.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyTyped(it) }
        } else {
            textField = TextField()
            if (scriptParameter.columns > 0) {
                textField.prefColumnCount = scriptParameter.columns
            }
        }
        textField.styleClass.add("expression")

        textField.text = scriptParameter.value
        textField.textProperty().bindBidirectional(scriptParameter.valueProperty)
        textField.textProperty().addListener({ _, _, _: String ->
            val error = scriptParameter.errorMessage()
            if (error == null) {
                clearError()
            } else {
                showError(error)
            }
        })

        this.textField = textField

        promptButton.onAction = EventHandler { event -> showScriptEditor() }

        return textField
    }

    override fun build(): ParameterField {
        val field = super.build()

        box?.let {
            it.graphic = promptButton
        }

        return field
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

    fun showScriptEditor() {
        val editor = VariablePrompter( textField, scriptParameter.scriptVariables)
        editor.build()
        editor.show()
    }
}
