package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.SingleSelectionModel
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, TabPane() {

    override fun addTool(tool: Tool) {
        val toolPane = ToolPane_Impl(tool)
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        getTabs().add(newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.getSelectedItem() as ProjectTab
    }

    override fun split(horizontal: Boolean) {
        currentTab()?.split(horizontal)
    }

    override fun splitToggle() {
        currentTab()?.splitToggle()
    }
}
