package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool

interface ProjectTab {

    val tabs: ProjectTabs

    val projectTabs: ProjectTabs

    val left: HalfTab

    val right: HalfTab?

    fun attached(projectTabs: ProjectTabs)

    fun detaching()

    fun add(tool: Tool)

    fun remove(toolPane: ToolPane)

    fun split(tool: Tool)

    fun split()

    fun splitToggle()

    fun duplicateTab()

    fun changed()
}
