package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.DragEvent

abstract class AbstractDropHelper : DropHelper {

    var targets = mutableSetOf<Node>()

    var excludes = mutableSetOf<Node>()

    override fun applyTo(target: Node): AbstractDropHelper {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }

        return this
    }

    override fun unapplyTo(target: Node) {
        target.onDragOver = null
        target.onDragEntered = null
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
            it.onDragEntered = null
            it.onDragExited = null
            it.onDragDropped = null
        }
    }

    fun onDragEntered(event: DragEvent) {
        event.consume()
    }

    abstract fun onDragOver(event: DragEvent): Boolean

    abstract fun onDragExited(event: DragEvent)

    abstract fun onDragDropped(event: DragEvent)

}
