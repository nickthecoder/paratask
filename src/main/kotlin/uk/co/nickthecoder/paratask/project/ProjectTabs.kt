package uk.co.nickthecoder.paratask.project

interface ProjectTabs {

    val projectWindow: ProjectWindow

    fun addTool(tool: uk.co.nickthecoder.paratask.Tool): ProjectTab

    fun addTool(index: Int, tool: uk.co.nickthecoder.paratask.Tool): ProjectTab

    fun addAfter(after: ProjectTab, tool: uk.co.nickthecoder.paratask.Tool): ProjectTab

    fun addToolPane(toolPane: ToolPane): ProjectTab

    fun addToolPane(index: Int, toolPane: ToolPane): ProjectTab

    fun removeTab(projectTab: ProjectTab)

    fun currentTab(): ProjectTab?

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun getScene(): javafx.scene.Scene

    fun listTabs(): List<ProjectTab>

}
