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
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.parameters.*

class MultipleField<T>(val multipleParameter: MultipleParameter<T>)
    : LabelledField(multipleParameter), ParameterListener, HasChildFields {

    val addButton = Button("+")

    val parametersForm = ParametersForm(multipleParameter)

    override val fieldSet: List<ParameterField>
        get() = parametersForm.fieldSet

    init {
        control = parametersForm
        parameter.parameterListeners.add(this)

        addButton.onAction = EventHandler {
            extraValue()
        }
        addButton.tooltip = Tooltip("Add")

        parametersForm.styleClass.add("multiple")
        println( "MF ${label} ${label.isVisible}")
    }

    fun buildContent() {
        parametersForm.clear()

        for ((index, innerParameter) in multipleParameter.innerParameters.withIndex()) {
            addParameter(innerParameter, index)
        }

        if (multipleParameter.innerParameters.isEmpty()) {
            parametersForm.add(addButton)
        }
    }

    fun addParameter(parameter: Parameter, index: Int): Node {

        val result = parametersForm.addParameter(parameter, index)

        val buttons = HBox()

        buttons.styleClass.add("multiple-line-buttons")

        val addButton = Button("+")
        addButton.onAction = EventHandler {
            newValue(index + 1)
        }
        addButton.tooltip = Tooltip("Insert Before")
        buttons.children.add(addButton)

        val removeButton = Button("-")
        removeButton.onAction = EventHandler {
            removeAt(index)
        }
        removeButton.tooltip = Tooltip("Remove")
        buttons.children.add(removeButton)

        if (result is LabelledField) {
            result.replaceLabel(buttons)
        }

        return result
    }

    private fun newValue(index: Int) {
        multipleParameter.newValue(index)
    }

    private fun extraValue() {
        newValue(multipleParameter.children.size)
    }

    fun removeAt(index: Int) {
        multipleParameter.removeAt(index)
    }

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.STRUCTURAL) {
            buildContent()
        }
    }
}
