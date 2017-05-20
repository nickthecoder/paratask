package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Tab

class ProjectTabs_Impl(override val projectWindow: ProjectWindow)

    : ProjectTabs, javafx.scene.control.TabPane() {

    init {
        selectionModel.selectedItemProperty().addListener(object : javafx.beans.value.ChangeListener<Tab> {
            override fun changed(ov: javafx.beans.value.ObservableValue<out Tab>?, oldTab: javafx.scene.control.Tab?, newTab: javafx.scene.control.Tab?) {
                onTabChanged(oldTab as ProjectTab_Impl?, newTab as ProjectTab_Impl?)
            }
        })
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
        getTabs().add(index, newProjectTab)

        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTabs.attaching ProjectTab")
        newProjectTab.attached(this)
        uk.co.nickthecoder.paratask.ParaTaskApp.Companion.logAttach("ProjectTabs.attached ProjectTab")

        selectionModel.clearAndSelect(index)
        return newProjectTab
    }

    override fun addTool(tool: uk.co.nickthecoder.paratask.Tool): ProjectTab {
        return addTool(tabs.size, tool)
    }

    override fun addTool(index: Int, tool: uk.co.nickthecoder.paratask.Tool): ProjectTab {
        return addToolPane(index, ToolPane_Impl(tool))
    }

    override fun addAfter(after: ProjectTab, tool: uk.co.nickthecoder.paratask.Tool): ProjectTab {
        val index = tabs.indexOf(after as javafx.scene.control.Tab)
        return addTool(index + 1, tool)
    }

    override fun currentTab(): ProjectTab? {
        return selectionModel.getSelectedItem() as ProjectTab
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
