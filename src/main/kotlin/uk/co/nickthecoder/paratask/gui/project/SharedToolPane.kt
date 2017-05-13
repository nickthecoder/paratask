package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.project.Tool

class SharedToolPane(override var tool: Tool) : ToolPane {

    val shared: ToolPane = tool.toolPane!!

    override val halfTab: HalfTab
        get() = shared.halfTab

    override val parametersPane: ParametersPane
        get() = shared.parametersPane

    override fun resultsTool(): Tool = shared.resultsTool()

    override fun updateResults(vararg allResults: Results) {}

    override fun attached(halfTab: HalfTab) {}

    override fun detaching() {}

    override fun toggleParameters() {
        shared.toggleParameters()
    }
}