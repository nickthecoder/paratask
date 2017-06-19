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

    open fun onDragOver(event: DragEvent) {
        if (accept(event)) {
            event.acceptTransferModes(* modes)
        }
        event.consume()
    }

    open fun onDragEntered(event: DragEvent) {
        if (accept(event)) {
            if (target is Styleable) {
                target.styleClass.add("drop")
            }
        }
        event.consume()
    }

    open fun onDragExited(event: DragEvent) {
        if (accept(event)) {
            if (target is Styleable) {
                target.styleClass.remove("drop")
            }
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

