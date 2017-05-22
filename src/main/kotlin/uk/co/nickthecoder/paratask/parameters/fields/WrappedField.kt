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

import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleButton
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.ValueParameter

class WrappedField(val parameterField: ParameterField) : TitledPane() {

    val parameter = parameterField.parameter

    val expressionButton: ToggleButton?

    val expressionField = TextField()

    val box = VBox()

    init {
        isCollapsible = false

        text = parameterField.parameter.label
        if (parameter is ValueParameter<*> && parameter.isProgrammingMode()) {
            expressionField.styleClass.add("expression")
            expressionButton = ToggleButton("=")
            box.children.add(expressionButton)
            expressionField.textProperty().bindBidirectional(parameter.expressionProperty)
            if (parameter.expression != null) {
                expressionButton.isSelected = true
                box.children.add(expressionField)
            } else {
                box.children.add(parameterField)
            }
            expressionButton.addEventHandler(ActionEvent.ACTION) { onExpression() }

            content = box
        } else {
            content = parameterField
            expressionButton = null
        }
    }

    fun onExpression() {
        if (expressionButton?.isSelected == true) {
            box.children.add(expressionField)
            box.children.remove(parameterField)
            expressionField.text = ""
        } else {
            box.children.remove(expressionField)
            box.children.add(parameterField)
            expressionField.text = null
        }
    }

}
