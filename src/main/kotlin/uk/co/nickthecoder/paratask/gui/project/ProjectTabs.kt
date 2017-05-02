package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.SingleSelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTabs(val projectWindow: ProjectWindow) {

    private val tabPane = TabPane()

    val node: Node = tabPane

    fun addTool(tool: Tool) {
        val toolPane = ToolPane(tool)
        val newProjectTab = ProjectTab(this, toolPane)
        tabPane.getTabs().add(newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")
    }

    fun currentTab(): ProjectTab? {
        val selection: SingleSelectionModel<Tab> = tabPane.selectionModel
        return selection.getSelectedItem() as ProjectTab
    }

    fun split(horizontal: Boolean) {
        currentTab()?.split(horizontal)
    }

    fun splitToggle() {
        currentTab()?.splitToggle()
    }
}
