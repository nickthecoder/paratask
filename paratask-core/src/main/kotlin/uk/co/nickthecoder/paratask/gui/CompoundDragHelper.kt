/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
