package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node

interface HalfTab {

    val toolPane: ToolPane

    var projectTab: ProjectTab

    fun attached(projectTab: ProjectTab)

    fun detaching()

}
