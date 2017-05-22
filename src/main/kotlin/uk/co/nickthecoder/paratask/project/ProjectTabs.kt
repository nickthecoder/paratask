package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool

interface ProjectTabs {

    val projectWindow: ProjectWindow

    fun addTool(tool: Tool, run: Boolean = true): ProjectTab

    fun addAfter(after: ProjectTab, tool: Tool, run: Boolean = true): ProjectTab

    fun removeTab(projectTab: ProjectTab)

    fun currentTab(): ProjectTab?

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun listTabs(): List<ProjectTab>

    fun nextTab()

    fun prevTab()

    fun selectTab(index: Int)
}
