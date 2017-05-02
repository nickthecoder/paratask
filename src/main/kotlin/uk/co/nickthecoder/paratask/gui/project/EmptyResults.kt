package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.Label

open class EmptyResults : Results {
    open override val node: Node = Label("")

    open override fun chooseFocus(toolPane: ToolPane): Node {
        return toolPane.halfTab.optionsField
    }

    open override fun attached(toolPane: ToolPane) {}

    open override fun detaching() {}
}