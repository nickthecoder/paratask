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
import javafx.scene.input.*

open class DragHelper<T>(
        val dataFormat: DataFormat,
        val source: Node,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val done: ((DragEvent, T) -> Unit)? = null,
        val obj: () -> (T)) {

    init {
        source.setOnDragDetected { onDragDetected(it) }
        source.setOnDragDone { onDone(it) }
    }

    open fun onDragDetected(event: MouseEvent) {
        val dragboard = source.startDragAndDrop(* modes)

        obj()?.let {
            val content = ClipboardContent()
            content.put(dataFormat, it)
            dragboard.setContent(content)

        }
        event.consume()
    }

    fun content(event: DragEvent): T {
        @Suppress("UNCHECKED_CAST")
        return event.dragboard.getContent(dataFormat) as T
    }

    open fun onDone(event: DragEvent) {

        done?.let { it(event, content(event)) }
        event.consume()
    }
}
