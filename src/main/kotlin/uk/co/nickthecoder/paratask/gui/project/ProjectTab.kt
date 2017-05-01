package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.control.Tab
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTab(val tabs: ProjectTabs, val toolPane: ToolPane) : Tab(toolPane.tool.shortTitle(), toolPane.node) {

    lateinit var projectTabs: ProjectTabs

    fun attached(projectTabs: ProjectTabs) {
        this.projectTabs = projectTabs

        println("ProjectTab.attaching ToolPane scene = ${toolPane.node.getScene()}")
        toolPane.attached(this)
        println("ProjectTab.attached ToolPane")
    }
}
