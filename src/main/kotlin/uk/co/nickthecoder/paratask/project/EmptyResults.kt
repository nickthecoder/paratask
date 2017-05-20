package uk.co.nickthecoder.paratask.project

open class EmptyResults(tool: uk.co.nickthecoder.paratask.Tool) : AbstractResults(tool) {

    open override val node: javafx.scene.Node = javafx.scene.control.Label("")
}
