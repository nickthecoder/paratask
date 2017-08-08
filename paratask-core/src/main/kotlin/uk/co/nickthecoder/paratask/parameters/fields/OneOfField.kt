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

import javafx.application.Platform
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.Parameter

class OneOfField(val oneOfParameter: OneOfParameter)
    : ParameterField(oneOfParameter), WrappableField {

    val choiceP = ChoiceParameter("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    val parametersForm = ParametersForm(oneOfParameter)

    private var wrappedField: WrappedField? = null

    init {
        control = parametersForm

        for (child in oneOfParameter.children) {
            choiceP.choice(child.name, child, child.label)
        }
        choiceP.valueProperty.bindBidirectional(oneOfParameter.valueProperty)
        choiceP.listen { onChanged() }
        choiceP.parent = oneOfParameter
    }

    fun buildContent() {
        parametersForm.clear()
        parametersForm.buildTop()

        parametersForm.addParameter(choiceP, 0)

        oneOfParameter.value?.let { child: Parameter ->
            if (!child.hidden) {
                parametersForm.addParameter(child, 1)
            }
        }
    }

    fun onChanged() {
        Platform.runLater {
            buildContent()
        }
    }

    override fun computePrefHeight(width: Double): Double {
        return insets.top + insets.bottom + (control?.prefHeight(width) ?: 0.0)
    }

    override fun computePrefWidth(height: Double): Double {
        return insets.left + insets.right + (control?.prefWidth(height) ?: 0.0)
    }

    override fun layoutChildren() {
        layoutInArea(control, insets.left, insets.top, width - insets.left - insets.right, height - insets.left - insets.right, 0.0, HPos.LEFT, VPos.CENTER)
    }

    override fun wrapper(): WrappedField {
        if (wrappedField == null) {
            wrappedField = WrappedField(this)
        }
        return wrappedField!!
    }

    override fun addAndRemoveButtons(buttons: Node) {
        wrapper().addAndRemoveButtons(buttons)
    }
}
