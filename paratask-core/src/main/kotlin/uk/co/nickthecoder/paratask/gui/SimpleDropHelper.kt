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

open class SimpleDropHelper<T>(
        val dataFormat: DataFormat,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val dropped: ((DragEvent, T) -> Boolean)? = null

) : AbstractDropHelper() {

    fun accept(event: DragEvent): Pair<Node?, Array<TransferMode>>? {
        debugPrintln("accept Target ${event.gestureTarget}")
        if (!event.dragboard.hasContent(dataFormat)) {
            debugPrintln("Not accepted : No data of the correct type")
            return null
        }
        val source = event.gestureSource
        if (excludes.contains(source)) {
            debugPrintln("Not accepted : Excluded node")
            return null
        }
        return acceptTarget(event)
    }

    open fun acceptTarget(event: DragEvent): Pair<Node?, Array<TransferMode>>? {
        debugPrintln("Accepted : $modes")
        return Pair(event.gestureTarget as Node?, modes)
    }

    override fun onDragDropped(event: DragEvent) {
        var success = false
        val accepted = accept(event)

        if (accepted != null) {
            success = onDropped(event, accepted.first)

            debugPrintln("Accepted : $modes")
        }

        event.isDropCompleted = success
        event.consume()
    }

    open fun onDropped(event: DragEvent, target: Node?): Boolean {
        dropped?.let {
            debugPrintln("Dropped. Calling dropped lambda")
            return it(event, content(event))
        }
        debugPrintln("Dropped. But no dropped lambda")
        return false
    }

    fun content(event: DragEvent): T {
        @Suppress("UNCHECKED_CAST")
        return event.dragboard.getContent(dataFormat) as T
    }

    private var previousStyledNode: Styleable? = null

    private fun setDropStyle(node: Styleable?, transferMode: TransferMode?) {

        previousStyledNode?.styleClass?.remove("drop-copy")
        previousStyledNode?.styleClass?.remove("drop-move")
        previousStyledNode?.styleClass?.remove("drop-link")

        val type = when (transferMode) {
            TransferMode.COPY -> "copy"
            TransferMode.MOVE -> "move"
            TransferMode.LINK -> "link"
            else -> null
        }

        previousStyledNode = node

        if (type != null) {
            previousStyledNode?.styleClass?.add("drop-$type")
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

    override fun onDragOver(event: DragEvent): Boolean {
        val accepted = accept(event)

        if (accepted != null) {
            val acceptedNode = accepted.first
            val acceptedModes = accepted.second
            event.acceptTransferModes(* acceptedModes)
            // We set the styles here, because onDragEntered is only called once, even if the transfer mode changes
            // (by holding down combinations of shift and ctrl)
            if (acceptedNode is Styleable) {
                setDropStyle(acceptedNode, eventTransferMode(acceptedModes, event))
            }
            return true
        } else {
            // Ensures that the old style is removed when dragging over a table where only some rows accept the drop.
            setDropStyle(null, null)
            return false
        }
    }

    override fun onDragExited(event: DragEvent) {
        setDropStyle(null, null)
        event.consume()
    }

}
