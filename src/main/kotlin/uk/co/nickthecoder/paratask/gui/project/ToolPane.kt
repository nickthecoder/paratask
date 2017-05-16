package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.project.Tool

interface ToolPane {

    var tool: Tool

    val halfTab: HalfTab

    val parametersPane: ParametersPane

    fun resultsTool(): Tool

    fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>)

    fun attached(halfTab: HalfTab)

    fun detaching()

    fun selected()

    fun toggleParameters()
}
