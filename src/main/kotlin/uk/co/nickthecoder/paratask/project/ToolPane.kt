package uk.co.nickthecoder.paratask.project

interface ToolPane {

    var tool: uk.co.nickthecoder.paratask.Tool

    val halfTab: HalfTab

    val parametersPane: ParametersPane

    fun resultsTool(): uk.co.nickthecoder.paratask.Tool

    fun replaceResults(resultsList: List<Results>, oldResultsList: List<Results>)

    fun attached(halfTab: HalfTab)

    fun detaching()

    fun selected()

    fun toggleParameters()
}
