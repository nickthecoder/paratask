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
package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.SimpleGroupParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.parameters.fields.ParametersForm
import uk.co.nickthecoder.paratask.table.filter.RowFilter

class FilterTab(val tool: Tool, filters: Map<String, RowFilter<*>>) : MinorTab("Filter") {

    val whole = BorderPane()

    var parametersPane: ParametersPane = ParametersPane_Impl(filters.values.first())

    val choiceP = ChoiceParameter("filter", label = "Select a Filter", value = filters.values.first())

    val choiceGroupP = SimpleGroupParameter("choiceGroup")

    val choiceForm = ParametersForm(choiceGroupP, null)

    init {
        content = whole
        whole.center = parametersPane as Node
        if (filters.size > 1) {
            filters.forEach { key, filter ->
                choiceP.addChoice(key, filter, filter.label)
            }
            choiceGroupP.addParameters(choiceP)
            choiceForm.buildContent()
            whole.top = choiceForm
            choiceP.listen {
                parametersPane.detaching()
                parametersPane = ParametersPane_Impl(choiceP.value!!)
                parametersPane.attached(tool.toolPane!!)
                whole.center = parametersPane as Node
            }
        }
    }

    override fun selected() {
        focus()
    }

    override fun focus() {
        if (tool.toolPane?.skipFocus != true) {
            Platform.runLater {
                ParaTaskApp.logFocus("FilterTab.focus. parametersPane.focus()")
                parametersPane.focus()
            }
        }
    }

    override fun deselected() {
    }
}
