package uk.co.nickthecoder.paratask.gui.project

import javafx.geometry.Orientation
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.task.HomeTool

interface ProjectTab {

    val tabs: ProjectTabs

    val projectTabs: ProjectTabs

    val left: HalfTab

    val right: HalfTab?

    fun attached(projectTabs: ProjectTabs)

    fun detaching()

    fun add(tool: Tool)

    fun remove(toolPane: ToolPane)

    fun split(tool: Tool)

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun changed()
}
