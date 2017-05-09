package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.project.Tool

interface ProjectTabs {

    val projectWindow: ProjectWindow

    fun addTool(tool: Tool): ProjectTab

    fun addToolPane(toolPane: ToolPane): ProjectTab

    fun removeTab(projectTab: ProjectTab)

    fun currentTab(): ProjectTab?

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun getScene(): Scene
}
