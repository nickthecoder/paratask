package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.project.Tool

open class EmptyResults(tool: Tool) : AbstractResults(tool) {

    open override val node: Node = Label("")
}
