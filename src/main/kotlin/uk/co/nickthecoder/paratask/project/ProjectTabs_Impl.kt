package uk.co.nickthecoder.paratask.project

import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, TabPane() {

    init {
        selectionModel.selectedItemProperty().addListener {
            _, oldTab, newTab -> onTabChanged(oldTab as ProjectTab_Impl?, newTab as ProjectTab_Impl?)
        }
    }

    private fun onTabChanged(oldTab: ProjectTab_Impl?, newTab: ProjectTab_Impl?) {
        oldTab?.deselected()
        newTab?.selected()
    }

    override fun addToolPane(toolPane: ToolPane): ProjectTab {
        return addToolPane(tabs.size, toolPane)
    }

    override fun addToolPane(index: Int, toolPane: ToolPane): ProjectTab {
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        tabs.add(index, newProjectTab)

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
        val index = tabs.indexOf(after as javafx.scene.control.Tab)
        return addTool(index + 1, tool)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.selectedItem as ProjectTab
    }

    override fun removeTab(projectTab: ProjectTab) {
        projectTab.detaching()
        tabs.remove<javafx.scene.control.Tab>(projectTab as javafx.scene.control.Tab)
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

    override fun listTabs(): List<ProjectTab> {
        return tabs.map { it as ProjectTab }
    }
}
