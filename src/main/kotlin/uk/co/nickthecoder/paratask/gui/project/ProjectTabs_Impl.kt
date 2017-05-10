package uk.co.nickthecoder.paratask.gui.project

import javafx.collections.ObservableList
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Tool

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, TabPane() {

    override fun addToolPane(toolPane: ToolPane): ProjectTab {
        return addToolPane(tabs.size, toolPane)
    }

    override fun addToolPane(index: Int, toolPane: ToolPane): ProjectTab {
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        getTabs().add(index, newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")

        selectionModel.clearAndSelect(index)
        return newProjectTab
    }

    override fun addTool(tool: Tool): ProjectTab {
        return addTool(tabs.size, tool)
    }

    override fun addTool(index: Int, tool: Tool): ProjectTab {
        return addToolPane(index, ToolPane_Impl(tool))
    }

    override fun addAfter(after: ProjectTab, tool: Tool): ProjectTab {
        val index = tabs.indexOf(after as Tab)
        return addTool(index + 1, tool)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.getSelectedItem() as ProjectTab
    }

    override fun removeTab(projectTab: ProjectTab) {
        projectTab.detaching()
        tabs.remove<Tab>(projectTab as Tab)
    }

    override fun split() {
        currentTab()?.split()
    }

    override fun splitToggle() {
        currentTab()?.splitToggle()
    }

    override fun duplicateTab() {
        currentTab()?.duplicateTab()
    }
}
