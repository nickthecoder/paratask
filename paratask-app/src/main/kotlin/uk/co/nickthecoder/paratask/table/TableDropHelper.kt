package uk.co.nickthecoder.paratask.table

import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.DropHelper

/**
 * T is the type of data send during drag and drop (it must be Serializable)
 * R is the type or Row
 */
abstract class TableDropHelper<T, R : Any>(
        dataFormat: DataFormat,
        val tool: TableTool<R>,
        modes: Array<TransferMode> = TransferMode.ANY)

    : DropHelper<T>(dataFormat, modes, null) {

    open fun acceptDropOnNonRow(): Array<TransferMode>? = modes

    open fun acceptDropOnRow(row: R): Array<TransferMode>? = acceptDropOnNonRow()

    override fun acceptTarget(event: DragEvent): Pair<Node?, Array<TransferMode>>? {

        val node = event.gestureTarget
        val r = tool.findTableRow(event)
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

    override fun onDropped(event: DragEvent, target: Node?): Boolean {
        if (target is TableRow<*>) {
            val r = tool.findTableRow(event)
            if (r.first != null) {
                return droppedOnRow(r.first!!, content(event), event.transferMode)
            }
        }

        return droppedOnNonRow(content(event), event.transferMode)
    }

    abstract fun droppedOnRow(row: R, content: T, transferMode: TransferMode): Boolean

    abstract fun droppedOnNonRow(content: T, transferMode: TransferMode): Boolean
}
