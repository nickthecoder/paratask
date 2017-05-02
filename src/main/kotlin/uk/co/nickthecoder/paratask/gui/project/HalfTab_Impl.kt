package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class HalfTab_Impl(override var toolPane: ToolPane)

    : BorderPane(), HalfTab {

    private val toolbar = ToolBar()

    override lateinit var projectTab: ProjectTab

    init {
        center = toolPane as Node
        bottom = toolbar
        toolbar.getItems().add(Label("Toolbar"))
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

    override fun changeTool(tool: Tool) {
        toolPane.detaching()
        children.remove(toolPane as Node)

        toolPane = ToolPane_Impl(tool)
        center = toolPane as Node
        toolPane.attached(this)
        
        projectTab.changed()
    }
}
