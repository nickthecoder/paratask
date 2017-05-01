package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.control.Tab
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.ParaTaskApp

class ProjectTab(val tabs: ProjectTabs, val toolPane: ToolPane) : Tab(toolPane.tool.shortTitle(), toolPane.node) {

    lateinit var projectTabs: ProjectTabs

    init {
        val image = ParaTaskApp.imageResource("tools/${toolPane.tool.task.taskD.name}.png")
        image?.let { setGraphic(ImageView(it)) }
    }

    fun attached(projectTabs: ProjectTabs) {
        this.projectTabs = projectTabs

        ParaTaskApp.logAttach("ProjectTab.attaching ToolPane scene = ${toolPane.node.getScene()}")
        toolPane.attached(this)
        ParaTaskApp.logAttach("ProjectTab.attached ToolPane")
    }
}
