package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.scene.Node
import uk.co.nickthecoder.paratask.ParaTaskApp

class ParametersTab(val parametersPane: ToolParametersPane) : MinorTab("Parameters") {

    init {
        content = parametersPane as Node
    }

    override fun focus() {
        if (parametersPane.tool.toolPane?.skipFocus != true) {
            Platform.runLater {
                ParaTaskApp.logFocus("ParametersTab.focus. parametersPane.focus()")
                parametersPane.focus()
            }
        }
    }

    override fun selected() {
        if (parametersPane.tool.toolPane?.skipFocus != true) {
            ParaTaskApp.logFocus("ParametersTab.selected focus()")
            focus()
        }
    }

    override fun deselected() {
    }
}

