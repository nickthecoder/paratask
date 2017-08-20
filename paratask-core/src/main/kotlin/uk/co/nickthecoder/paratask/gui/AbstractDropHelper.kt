package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.DragEvent

abstract class AbstractDropHelper {

    var targets = mutableSetOf<Node>()

    var excludes = mutableSetOf<Node>()


    fun applyTo(target: Node): AbstractDropHelper {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }

        return this
    }

    fun exclude(node: Node): AbstractDropHelper {
        excludes.add(node)
        return this
    }

    fun cancel() {
        targets.forEach {
            it.setOnDragOver(null)
            it.setOnDragEntered(null)
            it.setOnDragExited(null)
            it.setOnDragDropped(null)
        }
    }

    fun onDragEntered(event: DragEvent) {
        event.consume()
    }

    abstract fun onDragOver(event: DragEvent): Boolean

    abstract fun onDragExited(event: DragEvent)

    abstract fun onDragDropped(event: DragEvent)

}
