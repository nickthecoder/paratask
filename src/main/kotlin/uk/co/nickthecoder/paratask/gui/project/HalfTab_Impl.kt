package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp

class HalfTab_Impl(override val toolPane: ToolPane)

    : BorderPane(), HalfTab {

    private val toolbar = ToolBar()

    override lateinit var projectTab: ProjectTab

    init {
        center = toolPane as Node
        bottom = toolbar
    }

    override fun attached(projectTab: ProjectTab) {
        this.projectTab = projectTab

        ParaTaskApp.logAttach("HalfTab.attaching ToolPane")
        toolPane.attached(this)
        ParaTaskApp.logAttach("HalfTab.attached ToolPane")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("HalfTab.detaching ToolPane")
        toolPane.detaching()
        ParaTaskApp.logAttach("HalfTab.detached ToolPane")
    }
}
