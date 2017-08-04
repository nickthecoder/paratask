/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.gui

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

open class DropHelper<T>(
        val dataFormat: DataFormat,
        val target: Node,
        val source: Node = target,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val dropped: (DragEvent) -> Boolean
) {

    init {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
    }

    fun cancel() {
        target.setOnDragOver(null)
        target.setOnDragEntered(null)
        target.setOnDragExited(null)
        target.setOnDragDropped(null)
    }

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


    open fun accept(event: DragEvent): Array<TransferMode>? {
        return if (event.gestureSource !== source && event.dragboard.hasFiles()) modes else null
    }

    open fun onDragOver(event: DragEvent) {
        val acceptedModes = accept(event)

        if (acceptedModes != null) {
            event.acceptTransferModes(* acceptedModes)
            // We set the styles here, because onDragEntered is only called once, even if the transfer mode changes
            // (by holding down combinations of shift and ctrl)
            setDropStyle(styleableNode(event), eventTransferMode(acceptedModes, event))
        } else {
            // Ensures that the old style is removed when dragging over a table where only some rows accept the drop.
            setDropStyle(null, null)
        }
        event.consume()
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
            success = dropped(event)
        }

        event.isDropCompleted = success
        event.consume()
    }

    fun content(event: DragEvent): T {
        @Suppress("UNCHECKED_CAST")
        return event.dragboard.getContent(dataFormat) as T
    }
}
