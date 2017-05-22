package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool

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

    fun nextTab()

    fun prevTab()

    fun selectTab(index: Int)

    fun focusHeader()

    fun focusResults()

}
