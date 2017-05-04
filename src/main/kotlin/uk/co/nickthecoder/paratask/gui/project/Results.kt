package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node

interface Results {
    val node: Node

    fun attached(toolPane: ToolPane)

    fun detaching()
}
