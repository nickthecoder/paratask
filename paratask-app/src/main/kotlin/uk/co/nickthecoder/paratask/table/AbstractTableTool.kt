package uk.co.nickthecoder.paratask.table

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import uk.co.nickthecoder.paratask.AbstractTool

abstract class AbstractTableTool<R : Any> : AbstractTool(), TableTool<R> {

    open fun updateRow(tableRow: TableRow<WrappedRow<R>>, row: R) {
    }

    override fun createRow(): TableRow<WrappedRow<R>> = CustomTableRow()


    override fun findTableRow(event: DragEvent): Pair<R?, TableRow<WrappedRow<R>>?> {
        var node = event.pickResult.intersectedNode
        while (node != null) {
            if (node is TableView<*>) {
                return Pair(null, null)
            }
            if (node is TableRow<*>) {
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

    /**
     * Allows rows to be styled. For example, GitTool colours the row based on the state of the file
     */
    inner class CustomTableRow : TableRow<WrappedRow<R>>() {
        override fun updateItem(wrappedRow: WrappedRow<R>?, empty: Boolean) {
            super.updateItem(wrappedRow, empty)
            if (!empty && wrappedRow != null) {
                updateRow(this, wrappedRow.row)
            }
        }
    }
}
