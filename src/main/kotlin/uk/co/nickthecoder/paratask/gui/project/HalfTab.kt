package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.control.TextField
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.Tool

interface HalfTab {

    val toolPane: ToolPane

    var projectTab: ProjectTab

    val optionsField: TextField

    fun attached(projectTab: ProjectTab)

    fun detaching()

    fun changeTool(tool: Tool, values: Values? = null)

    fun pushHistory(tool: Tool, values: Values)
}