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
import javafx.scene.input.TransferMode
import javafx.scene.Node
import javafx.scene.input.DragEvent
import java.io.File

open class DropFiles(
        val target: Node,
        val source: Node = target,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val dropped: (List<File>, TransferMode) -> Boolean

) {

    init {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
    }

    open fun accept(event: DragEvent): Boolean {
        return event.gestureSource != source && event.dragboard.hasFiles()
    }

    private fun setDropStyle(transferMode: TransferMode?) {
        val type = when (transferMode) {
            TransferMode.COPY -> "copy"
            TransferMode.MOVE -> "move"
            TransferMode.LINK -> "link"
            else -> null
        }

        if (target is Styleable) {
            target.styleClass.remove("drop-copy")
            target.styleClass.remove("drop-move")
            target.styleClass.remove("drop-link")
            if (type != null) {
                target.styleClass.add("drop-$type")
            }
        }
    }

    /**
     * When the drag component allows for ANY modes, but the drop component only allows LINK, then
     * during onDragEventEntered, event.transferMode is COPY, despite the mouse pointer showing a LINK.
     * I think this is a bug, and this is my best effort work-around.
     */
    private fun eventTransferMode(event: DragEvent): TransferMode? {
        if (!modes.contains(event.transferMode)) {
            return modes.firstOrNull() ?: event.transferMode
        }
        return event.transferMode
    }

    open fun onDragOver(event: DragEvent) {
        if (accept(event)) {
            event.acceptTransferModes(* modes)
            // We set the styles here, because onDragEntered is only called once, even if the transfer mode changes
            // (by holding down combinations of shift and ctrl)
            setDropStyle(eventTransferMode(event))
        }
        event.consume()
    }

    open fun onDragEntered(event: DragEvent) {
        event.consume()
    }

    open fun onDragExited(event: DragEvent) {
        if (accept(event)) {
            setDropStyle(null)
        }
        event.consume()
    }

    open fun onDragDropped(event: DragEvent) {
        val dragboard = event.dragboard
        var success = false
        if (accept(event)) {
            success = dropped(dragboard.files, event.transferMode)
        }

        event.isDropCompleted = success
        event.consume()
    }

}

