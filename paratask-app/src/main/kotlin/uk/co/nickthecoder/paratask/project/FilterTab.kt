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
