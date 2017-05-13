package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.Labelled

interface Results : Labelled {

    val tool: Tool

    val node: Node

    fun attached(toolPane: ToolPane)

    fun detaching()

    fun focus()
}
