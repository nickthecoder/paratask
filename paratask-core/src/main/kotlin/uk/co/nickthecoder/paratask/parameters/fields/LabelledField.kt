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

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.parameters.Parameter

interface LabelledField {

    var labelNode: Node
}

abstract class AbstractLabelledField(parameter: Parameter, label: String = parameter.label, isBoxed: Boolean = false)
    : ParameterField(parameter, isBoxed = isBoxed), LabelledField {

    private var stack = StackPane()

    private var label = Label(label)

    override var labelNode: Node = stack

    override fun build(): AbstractLabelledField {
        super.build()

        stack.children.add(label)
        stack.alignment = Pos.CENTER_LEFT

        if (parameter.description != "") {
            label.tooltip = Tooltip(parameter.description)
        }

        return this
    }

    override fun plusMinusButtons(buttons: Node) {
        stack.children.clear()
        stack.children.add(buttons)
    }

    fun showOrClearError(message: String?) {
        if (message == null) {
            clearError()
        } else {
            showError(message)
        }
    }

}
