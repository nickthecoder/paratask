package uk.co.nickthecoder.paratask.project

interface ProjectTab {

    val tabs: ProjectTabs

    val projectTabs: ProjectTabs

    val left: HalfTab

    val right: HalfTab?

    fun attached(projectTabs: ProjectTabs)

    fun detaching()

    fun add(tool: uk.co.nickthecoder.paratask.Tool)

    fun remove(toolPane: ToolPane)

    fun split(tool: uk.co.nickthecoder.paratask.Tool)

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun changed()
}
