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
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import uk.co.nickthecoder.paratask.parameters.Parameter

interface LabelledField {

    var label: Node

    fun replaceLabel(node: Node)
}

abstract class AbstractLabelledField(parameter: Parameter, label: String = parameter.label)
    : ParameterField(parameter), LabelledField {

    override var label: Node = Label(label)

    override fun build(): AbstractLabelledField {
        super.build()

        if (parameter.description != "") {
            (label as Label).tooltip = Tooltip(parameter.description)
        }

        return this
    }

    /**
     * When placed in a MultipleField, the label is replaced by "+" and "-" buttons
     */
    override fun replaceLabel(node: Node) {
        label = node
    }

    fun showOrClearError(message: String?) {
        if (message == null) {
            clearError()
        } else {
            showError(message)
        }
    }

}
