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
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.parameters.*

abstract class ParameterField(val parameter: Parameter) : ParameterListener {

    lateinit var fieldParent: FieldParent

    internal var expressionButton: ToggleButton? = null

    internal var expressionField: TextField? = null

    val error = Label()

    var control: Node? = null

    /**
     * When in programming mode, this will contain the "=" button, and the expression or the control.
     * Otherwise it is just the control
     */
    var controlContainer: Node? = null

    open fun build(): ParameterField {

        if (control != null) {
            throw IllegalStateException("Field has already been built")
        }

        error.isVisible = false
        error.styleClass.add("error")

        parameter.parameterListeners.add(this)

        control = createControl()

        if (parameter.isProgrammingMode() && parameter is ValueParameter<*>) {
            val box = HBox()
            box.styleClass.add("programming")
            val stack = StackPane()
            stack.alignment = Pos.CENTER_LEFT

            expressionField = TextField()
            expressionField?.styleClass?.add("expression")
            expressionButton = ToggleButton("=")

            box.children.addAll(expressionButton, stack)
            stack.children.addAll(expressionField, control)
            onExpression()

            if (parameter.expression != null) {
                expressionButton?.isSelected = true
            }
            expressionField?.textProperty()?.bindBidirectional(parameter.expressionProperty)
            expressionButton?.addEventHandler(ActionEvent.ACTION) { onExpression() }

            expressionField?.isVisible = parameter.expression != null
            control?.isVisible = parameter.expression == null

            controlContainer = box
        } else {
            controlContainer = control
        }

        return this
    }

    abstract fun createControl(): Node

    fun showError(message: String) {
        error.text = message
        if (!error.isVisible) {
            error.isVisible = true
            fieldParent.updateField(this)
        }
    }

    fun clearError() {
        if (error.isVisible) {
            error.isVisible = false
            fieldParent.updateField(this)
        }
    }

    open fun isDirty(): Boolean = false

    open fun updateEnabled() {
        control?.isDisable = !parameter.enabled
    }

    open fun plusMinusButtons(buttons: Node) {}

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.ENABLED) {
            updateEnabled()
        }
        if (event.type == ParameterEventType.VISIBILITY) {
            fieldParent.updateField(this)
        }
    }

    fun onExpression() {
        if (expressionButton?.isSelected == true) {
            expressionField?.text = ""
        } else {
            expressionField?.text = null
        }
        control?.isVisible = expressionButton?.isSelected == false
        expressionField?.isVisible = expressionButton?.isSelected == true
    }
}
