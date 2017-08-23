package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.DragEvent

abstract class AbstractDropHelper : DropHelper {

    var targets = mutableSetOf<Node>()

    var excludes = mutableSetOf<Node>()

    var debug = false

    fun debugPrintln(str: String) {
        if (debug) {
            println(str)
        }
    }

    override fun applyTo(target: Node): AbstractDropHelper {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }

        return this
    }

    override fun unapplyTo(target: Node) {
        target.onDragOver = null
        target.onDragExited = null
        target.onDragDropped = null
    }

    override fun exclude(node: Node): AbstractDropHelper {
        excludes.add(node)
        return this
    }

    override fun cancel() {
        targets.forEach {
            it.onDragOver = null
            it.onDragExited = null
            it.onDragDropped = null
        }
    }

}
