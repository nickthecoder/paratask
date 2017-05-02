package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.project.Tool

interface HalfTab {

    val toolPane: ToolPane

    var projectTab: ProjectTab

    fun attached(projectTab: ProjectTab)

    fun detaching()

    fun changeTool( tool : Tool )

}
