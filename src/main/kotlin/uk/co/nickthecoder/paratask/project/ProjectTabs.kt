package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool

interface ProjectTabs {

    val projectWindow: ProjectWindow

    fun addTool(tool: Tool): ProjectTab

    fun addTool(index: Int, tool: Tool): ProjectTab

    fun addAfter(after: ProjectTab, tool : Tool): ProjectTab

    fun addToolPane(toolPane: ToolPane): ProjectTab

    fun addToolPane(index: Int, toolPane: ToolPane): ProjectTab

    fun removeTab(projectTab: ProjectTab)

    fun currentTab(): ProjectTab?

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun listTabs(): List<ProjectTab>

}
