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
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.LabelPosition
import uk.co.nickthecoder.paratask.parameters.ParameterListener

open class VerticalGroupField(
        val groupParameter: GroupParameter,
        val labelPosition: LabelPosition,
        isBoxed: Boolean)

    : ParameterField(groupParameter, isBoxed = isBoxed), FieldParent, ParameterListener {

    val fieldSet = mutableListOf<ParameterField>()

    override fun iterator(): Iterator<ParameterField> {
        return fieldSet.iterator()
    }

    override fun createControl(): Node {

        val vertical = VBox()
        vertical.styleClass.add("vertical-group")

        groupParameter.children.forEach { child ->
            val field = child.createField()
            field.fieldParent = this

            val container = createChild(field)

            fieldSet.add(field)
            vertical.children.add(container)
        }

        return vertical
    }

    fun createChild(childField: ParameterField): Node {

        val container = BorderPane()
        if (labelPosition != LabelPosition.NONE) {
            container.top = childField.label
        }
        if (childField.parameter.isStretchy()) {
            container.center = childField.control
        } else {
            container.center = HBox(childField.control)
        }
        BorderPane.setAlignment(childField.control, Pos.CENTER_LEFT)

        return container
    }

    override fun updateError(field: ParameterField) {
        field.control?.parent?.let {
            var parent = it
            if (parent is HBox) {
                parent = parent.parent
            }
            if (parent is BorderPane) {
                if (field.error.isVisible) {
                    parent.bottom = field.error
                    BorderPane.setAlignment(field.error, Pos.CENTER_LEFT)
                } else {
                    parent.bottom = null
                }
            }
        }
    }

    override fun updateField(field: ParameterField) {
    }

}
