package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.project.Tool

abstract class AbstractResults(
        override val tool: Tool,
        override val label: String = "Results")

    : Results {

    open override fun attached(toolPane: ToolPane) {}

    open override fun detaching() {}

    override fun focus() {
        node.requestFocus()
    }

}