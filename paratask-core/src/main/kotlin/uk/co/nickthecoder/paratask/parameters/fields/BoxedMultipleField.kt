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
import javafx.scene.layout.HBox
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.Parameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType
import uk.co.nickthecoder.paratask.util.focusNext

class BoxedMultipleField<T>(val multipleParameter: MultipleParameter<T>)
    : ParameterField(multipleParameter), HasChildFields, WrappableField {

    val addButton = Button("+")

    val parametersForm = ParametersForm(multipleParameter)

    val shortcuts = ShortcutHelper("MultipleField", this, false)

    private var wrappedField: WrappedField? = null

    init {
        shortcuts.add(ApplicationActions.NEW_ITEM) { extraValue() }
    }

    override fun wrapper(): WrappedField {
        if (wrappedField == null) {
            wrappedField = WrappedField(this)
            wrappedField!!.box.styleClass.add("padded-box")
        }
        return wrappedField!!
    }

    override fun addAndRemoveButtons(buttons: Node) {
        wrapper().addAndRemoveButtons(buttons)
    }

    override val fieldSet: List<ParameterField>
        get() = parametersForm.fieldSet

    override fun createControl(): ParametersForm {

        control = parametersForm

        addButton.onAction = EventHandler {
            extraValue()
        }
        addButton.tooltip = Tooltip("Add")

        parametersForm.styleClass.add("multiple")

        // Add 'blank' items, so that the required minumum number of items can be entered.
        // The most common scenarios, is adding a single 'blank' item when minItems == 1
        while (multipleParameter.minItems > multipleParameter.value.size) {
            multipleParameter.newValue()
        }

        buildContent()
        return parametersForm
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
        } else if (result is WrappedField) {
            result.addAndRemoveButtons(buttons)
        }

        return result
    }

    private fun newValue(index: Int) {
        val valueParameter = multipleParameter.newValue(index)
        parametersForm.findField(valueParameter)?.focusNext()
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
