package uk.co.nickthecoder.paratask.project

import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.Tool

interface HalfTab {

    val toolPane: ToolPane

    var projectTab: ProjectTab

    val optionsField: TextField

    val toolBars: BorderPane

    val history: History

    fun attached(projectTab: ProjectTab)

    fun detaching()

    fun changeTool(tool: Tool, prompt: Boolean = false)

    fun pushHistory()

    fun pushHistory(tool: Tool)

    fun onStop()

    fun onRun()

    fun onClose()

    fun focusOption()

    fun focusOtherHalf()

}
