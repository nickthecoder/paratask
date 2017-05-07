package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import javafx.scene.control.Label

open class EmptyResults : AbstractResults() {

    open override val node: Node = Label("")
}