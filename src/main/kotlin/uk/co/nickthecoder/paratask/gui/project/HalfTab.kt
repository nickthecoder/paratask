package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.project.Tool

interface HalfTab {

    val toolPane: ToolPane

    var projectTab: ProjectTab

    val optionsField: TextField

    val toolBars: BorderPane

    fun attached(projectTab: ProjectTab)

    fun detaching()

    fun changeTool(tool: Tool)

    fun pushHistory()

    fun pushHistory(tool: Tool)
}