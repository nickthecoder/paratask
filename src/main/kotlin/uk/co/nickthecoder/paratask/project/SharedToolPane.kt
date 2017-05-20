package uk.co.nickthecoder.paratask.project

class SharedToolPane(override var tool: uk.co.nickthecoder.paratask.Tool) : ToolPane {

    val shared: ToolPane = tool.toolPane!!

    override val halfTab: HalfTab
        get() = shared.halfTab

    override val parametersPane: ParametersPane
        get() = shared.parametersPane

    override fun resultsTool(): uk.co.nickthecoder.paratask.Tool = shared.resultsTool()

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