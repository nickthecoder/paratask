package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

interface ToolPane {

    var tool: Tool

    val halfTab: HalfTab

    val parametersPane: ParametersPane

    var values: Values

    fun updateResults(results: Results)

    fun attached(halfTab: HalfTab)

    fun detaching()

    fun copy(): ToolPane

    fun showJustResults()

    fun showJustParameters()

    fun showBoth()

    fun toggleParameters()

    fun notBoth()

    fun cycle()
}
