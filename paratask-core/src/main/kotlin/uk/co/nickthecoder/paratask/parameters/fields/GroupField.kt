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

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.AbstractGroupParameter

/**
 * This is the Field created by GroupParameter and is also use in TaskForm to prompt a whole Task.
 */
open class GroupField(groupParameter: AbstractGroupParameter)
    : ParameterField(groupParameter), WrappableField, HasChildFields {

    val parametersForm = ParametersForm(groupParameter)

    private var wrappedField: WrappedField? = null

    override fun createControl(): ParametersForm {
        buildContent()
        return parametersForm
    }

    override val fieldSet: List<ParameterField>
        get() = parametersForm.fieldSet

    fun buildContent() {
        parametersForm.buildContent()
    }

    override fun wrapper(): WrappedField {
        if (wrappedField == null) {
            wrappedField = WrappedField(this)
        }
        return wrappedField!!
    }

    override fun computePrefHeight(width: Double): Double {
        return insets.top + insets.bottom + (control?.prefHeight(width) ?: 0.0)
    }

    override fun computePrefWidth(height: Double): Double {
        return insets.left + insets.right + (control?.prefWidth(height) ?: 0.0)
    }

    override fun layoutChildren() {
        layoutInArea(
                control,
                insets.left, insets.top,
                width - insets.left - insets.right,
                height - insets.top - insets.bottom,
                0.0, HPos.LEFT, VPos.CENTER)
    }

    override fun addAndRemoveButtons(buttons: Node) {
        wrapper().addAndRemoveButtons(buttons)
    }
}
