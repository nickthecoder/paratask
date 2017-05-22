package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, TabPane() {

    init {
        selectionModel.selectedItemProperty().addListener {
            _, oldTab, newTab ->
            onTabChanged(oldTab as ProjectTab_Impl?, newTab as ProjectTab_Impl?)
        }
    }

    private fun onTabChanged(oldTab: ProjectTab_Impl?, newTab: ProjectTab_Impl?) {
        oldTab?.deselected()
        newTab?.selected()
    }

    private fun addTool(index: Int, tool: Tool, run: Boolean): ProjectTab {
        val toolPane = ToolPane_Impl(tool)
        val newProjectTab = ProjectTab_Impl(this, toolPane)
        tabs.add(index, newProjectTab)

        ParaTaskApp.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        ParaTaskApp.logAttach("ProjectTabs.attached ProjectTab")

        selectionModel.clearAndSelect(index)

        toolPane.halfTab.pushHistory()
        if (run) {
            toolPane.tool.taskRunner.run()
        }
        return newProjectTab
    }

    override fun addTool(tool: Tool, run: Boolean): ProjectTab {
        return addTool(tabs.size, tool, run = run)
    }

    override fun addAfter(after: ProjectTab, tool: Tool, run: Boolean): ProjectTab {
        val index = tabs.indexOf(after as Tab)
        return addTool(index + 1, tool, run = run)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.selectedItem as ProjectTab
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

    override fun listTabs(): List<ProjectTab> {
        return tabs.map { it as ProjectTab }
    }

    override fun nextTab() {
        if ( tabs.isNotEmpty()) {
            var index = selectionModel.selectedIndex + 1
            if (index >= tabs.size) index = 0

            selectionModel.clearAndSelect(index)
        }
    }

    override fun prevTab() {
        if ( tabs.isNotEmpty()) {
            var index = selectionModel.selectedIndex - 1
            if (index < 0 ) index = tabs.size - 1

            selectionModel.clearAndSelect(index)
        }
    }

    override fun selectTab(index: Int) {
        if (index >= 0 && index < tabs.size) {
            selectionModel.clearAndSelect(index)
        }
    }


}
