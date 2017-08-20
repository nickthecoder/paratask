package uk.co.nickthecoder.paratask.gui

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

abstract class AbstractDropHelper(val modes: Array<TransferMode> = TransferMode.ANY) {

    var target: Node? = null

    var source: Node? = null

    /**
     * Which node should we style when a drag occurs.
     * Usually this is the target node, but in the case of a table, sometime it is the TableRow the mouse is
     * hovering over.
     */
    open fun styleableNode(event: DragEvent): Styleable? = if (target is Styleable) target else null

    private var styledNode: Styleable? = null

    private fun setDropStyle(node: Styleable?, transferMode: TransferMode?) {

        styledNode?.styleClass?.remove("drop-copy")
        styledNode?.styleClass?.remove("drop-move")
        styledNode?.styleClass?.remove("drop-link")

        val type = when (transferMode) {
            TransferMode.COPY -> "copy"
            TransferMode.MOVE -> "move"
            TransferMode.LINK -> "link"
            else -> null
        }

        styledNode = node

        if (type != null) {
            styledNode?.styleClass?.add("drop-$type")
        }
    }

    fun applyTo(target: Node, source: Node = target) {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
        this.source = source
    }

    fun cancel() {
        target?.setOnDragOver(null)
        target?.setOnDragEntered(null)
        target?.setOnDragExited(null)
        target?.setOnDragDropped(null)
    }

    abstract fun accept(event: DragEvent): Array<TransferMode>?

    /**
     * When the drag component allows for ANY modes, but the drop component only allows LINK, then
     * during onDragEventEntered, event.transferMode is COPY, despite the mouse pointer showing a LINK.
     * I think this is a bug, and this is my best effort work-around.
     */
    private fun eventTransferMode(allowedModes: Array<TransferMode>, event: DragEvent): TransferMode? {
        if (!allowedModes.contains(event.transferMode)) {
            return allowedModes.firstOrNull() ?: event.transferMode
        }
        return event.transferMode
    }

    open fun onDragOver(event: DragEvent): Boolean {
        val acceptedModes = accept(event)

        if (acceptedModes != null) {
            event.acceptTransferModes(* acceptedModes)
            // We set the styles here, because onDragEntered is only called once, even if the transfer mode changes
            // (by holding down combinations of shift and ctrl)
            setDropStyle(styleableNode(event), eventTransferMode(acceptedModes, event))
            return true
        } else {
            // Ensures that the old style is removed when dragging over a table where only some rows accept the drop.
            setDropStyle(null, null)
            return false
        }
    }

    open fun onDragEntered(event: DragEvent) {
        event.consume()
    }

    open fun onDragExited(event: DragEvent) {
        setDropStyle(null, null)
        event.consume()
    }

    open fun onDragDropped(event: DragEvent) {
        var success = false
        if (accept(event) != null) {
            success = onDropped(event)
        }

        event.isDropCompleted = success
        event.consume()
    }

    abstract fun onDropped(event: DragEvent): Boolean
}
