package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTabs(val projectWindow: ProjectWindow) {

    private val tabPane = TabPane()

    val node: Node = tabPane

    fun addTool(tool: Tool) {
        val toolPane = ToolPane(tool)
        println("ProjectTabs.addTool scene = ${tabPane.getScene()}")
        val newProjectTab = ProjectTab(this, toolPane)
        tabPane.getTabs().add(newProjectTab)

        println("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        println("ProjectTabs.attached ProjectTab")
    }
}
