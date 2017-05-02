package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

interface ToolPane {

    var tool: Tool

    val halfTab: HalfTab

    var values: Values

    fun updateResults(results: Results)

    fun attached(halfTab: HalfTab)

    fun detaching()

}
