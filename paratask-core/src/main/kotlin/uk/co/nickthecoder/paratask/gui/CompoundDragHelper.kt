package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode

class CompoundDragHelper(vararg helpers: SimpleDragHelper<*>) : DragHelper {

    private val dragHelpers: List<SimpleDragHelper<*>>

    val modes: Array<TransferMode>

    init {
        val set = mutableSetOf<TransferMode>()
        dragHelpers = helpers.asList<SimpleDragHelper<*>>()
        dragHelpers.forEach {
            it.modes.forEach { set.add(it) }
        }
        modes = set.toTypedArray()
    }

    override fun applyTo(node: Node) {
        node.setOnDragDetected { onDragDetected(it) }
        node.setOnDragDone { onDone(it) }
    }

    fun onDragDetected(event: MouseEvent) {
        val dragboard = event.pickResult.intersectedNode.startDragAndDrop(* modes)

        val clipboard = ClipboardContent()

        dragHelpers.forEach {
            it.addContentToClipboard(clipboard)
        }
        dragboard.setContent(clipboard)
        event.consume()
    }

    fun onDone(event: DragEvent) {

        dragHelpers.forEach {
            it.onDone(event)
        }
        event.consume()
    }

}
