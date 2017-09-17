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
package uk.co.nickthecoder.paratask.table

import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.SimpleDropHelper

/**
 * T is the type of data send during drag and drop (it must be Serializable)
 * R is the type or Row
 */
abstract class TableDropHelper<T, R : Any>(
        dataFormat: DataFormat,
        modes: Array<TransferMode> = TransferMode.ANY)

    : SimpleDropHelper<T>(dataFormat, modes, null) {

    open fun acceptDropOnNonRow(): Array<TransferMode>? = modes

    open fun acceptDropOnRow(row: R): Array<TransferMode>? = acceptDropOnNonRow()

    override fun acceptTarget(event: DragEvent): Pair<Node?, Array<TransferMode>>? {

        val node = event.gestureTarget
        val r = findTableRow(event)
        val row = r.first
        val tableRow = r.second

        if (tableRow != null && row != null) {
            val rowModes = acceptDropOnRow(row)
            if (rowModes != null) {
                return Pair(tableRow, rowModes)
            }
        }

        val nonRowModes = acceptDropOnNonRow()
        if (nonRowModes != null) {
            return Pair(node as Node?, nonRowModes)
        }

        return null
    }

    override fun onDropped(event: DragEvent, target: Node?) {
        if (target is TableRow<*>) {
            val r = findTableRow(event)
            if (r.first != null) {
                droppedOnRow(r.first!!, content(event), event.transferMode)
                return
            }
        }

        droppedOnNonRow(content(event), event.transferMode)
    }

    abstract fun droppedOnRow(row: R, content: T, transferMode: TransferMode)

    abstract fun droppedOnNonRow(content: T, transferMode: TransferMode)


    fun findTableRow(event: DragEvent): Pair<R?, TableRow<WrappedRow<R>>?> {
        var node = event.pickResult.intersectedNode
        while (node != null) {
            if (node is TableView<*>) {
                return Pair(null, null)
            }
            if (node is TableRow<*>) {
                @Suppress("UNCHECKED_CAST")
                val tableRow = node as TableRow<WrappedRow<R>>
                if (tableRow.isEmpty) {
                    return Pair(null, null)
                }
                return Pair(tableRow.item.row, tableRow)
            }
            node = node.parent
        }
        return Pair(null, null)
    }

}
