package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.project.Tool

interface ProjectTabs {

    val projectWindow: ProjectWindow

    fun addTool(tool: Tool)

    fun currentTab(): ProjectTab?

    fun split(horizontal: Boolean)

    fun splitToggle()

    fun getScene(): Scene
}
