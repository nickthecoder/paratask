package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp

class HalfTab(val toolPane: ToolPane) : BorderPane() {

    val toolbar = ToolBar()

    lateinit var projectTab: ProjectTab

    init {
        center = toolPane.node
        bottom = toolbar
    }

    fun attached(projectTab: ProjectTab) {
        this.projectTab = projectTab

        ParaTaskApp.logAttach("HalfTab.attaching ToolPane")
        toolPane.attached(this)
        ParaTaskApp.logAttach("HalfTab.attached ToolPane")
    }

    fun detaching() {
        ParaTaskApp.logAttach("HalfTab.detaching ToolPane")
        toolPane.detaching()
        ParaTaskApp.logAttach("HalfTab.detached ToolPane")
    }
}
