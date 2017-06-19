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

import javafx.scene.Node
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import java.io.File

open class DragFiles(
        val source: Node,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val files: () -> (List<File>?)) {

    init {
        source.setOnDragDetected { onDragDetected(it) }
        source.setOnDragDone { onDone(it) }
    }

    open fun onDragDetected(event: MouseEvent) {
        val dragboard = source.startDragAndDrop(* modes)

        files()?.let {
            val content = ClipboardContent()
            content.putFiles(it)
            dragboard.setContent(content)

        }
        event.consume()
    }

    open fun onDone(event: DragEvent) {
        event.consume()
    }
}
