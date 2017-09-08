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
import uk.co.nickthecoder.paratask.parameters.*

class OneOfField(val oneOfParameter: OneOfParameter)
    : ParameterField(oneOfParameter), WrappableField {

    val choiceP = ChoiceParameter("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    val parametersForm = ParametersForm(oneOfParameter)

    private var wrappedField: WrappedField? = null

    override fun createControl(): ParametersForm {

        choiceP.parameterListeners.add(this)

        for (child in oneOfParameter.children) {
            choiceP.choice(child.name, child, child.label)
        }
        choiceP.valueProperty.bindBidirectional(oneOfParameter.valueProperty)
        choiceP.parent = oneOfParameter

        buildContent()
        return parametersForm
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

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.VALUE) {
            buildContent()
        }
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
