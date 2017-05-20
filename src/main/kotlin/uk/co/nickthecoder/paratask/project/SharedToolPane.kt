package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool

class SharedToolPane(override var tool: Tool) : ToolPane {

    val shared: ToolPane = tool.toolPane!!

    override val halfTab: HalfTab
        get() = shared.halfTab

    override val parametersPane: ParametersPane
        get() = shared.parametersPane

    override fun resultsTool(): Tool = shared.resultsTool()

    override fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>) {
        shared.replaceResults(resultsList, oldResultsList)
    }

    override fun attached(halfTab: HalfTab) {}

    override fun detaching() {}

    override fun selected() {}

    override fun toggleParameters() {
        shared.toggleParameters()
    }
}