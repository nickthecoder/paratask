package uk.co.nickthecoder.paratask

import javafx.scene.Node
import uk.co.nickthecoder.paratask.util.focusNext

interface SidePanel {

    val node: Node

    fun focus() = node.focusNext()

}
