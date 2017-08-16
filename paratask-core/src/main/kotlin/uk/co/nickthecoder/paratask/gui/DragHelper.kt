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
        allowCopy: Boolean = true,
        allowMove: Boolean = true,
        allowLink: Boolean = true,
        val onMoved: ((T) -> Unit)? = null,
        val obj: () -> (T)) {

    val modes = modes(allowCopy, allowMove, allowLink)

    fun applyTo(node: Node) {
        node.setOnDragDetected { onDragDetected(it) }
        node.setOnDragDone { onDone(it) }
    }

    fun addContentToClipboard(clipboard: ClipboardContent): Boolean {
        obj()?.let {
            clipboard.put(dataFormat, it)
            return true
        }
        return false
    }

    open fun onDragDetected(event: MouseEvent) {
        val dragboard = event.pickResult.intersectedNode.startDragAndDrop(* modes)

        val clipboard = ClipboardContent()
        if (addContentToClipboard(clipboard)) {
            dragboard.setContent(clipboard)
        }
        event.consume()
    }

    open fun onDone(event: DragEvent) {

        val content = content(event)

        if (content != null && event.transferMode == TransferMode.MOVE) {
            onMoved?.let {
                it(content)
            }
        }
        event.consume()
    }


    fun content(event: DragEvent): T? {
        @Suppress("UNCHECKED_CAST")
        return event.dragboard.getContent(dataFormat) as T
    }

    companion object {
        fun modes(allowCopy: Boolean, allowMove: Boolean, allowLink: Boolean): Array<TransferMode> {
            val set = mutableSetOf<TransferMode>()
            if (allowCopy) {
                set.add(TransferMode.COPY)
            }
            if (allowMove) {
                set.add(TransferMode.MOVE)
            }
            if (allowLink) {
                set.add(TransferMode.LINK)
            }
            return set.toTypedArray()
        }
    }
}
