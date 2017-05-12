package uk.co.nickthecoder.paratask.gui.project

abstract class AbstractResults(override val label: String = "Results") : Results {

    open override fun attached(toolPane: ToolPane) {}

    open override fun detaching() {}

    override fun focus() {
        node.requestFocus()
    }

}