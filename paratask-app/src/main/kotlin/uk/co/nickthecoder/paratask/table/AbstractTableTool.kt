package uk.co.nickthecoder.paratask.table

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import uk.co.nickthecoder.paratask.AbstractTool

abstract class AbstractTableTool<R : Any> : AbstractTool(), TableTool<R> {

    open fun updateRow(tableRow: TableRow<WrappedRow<R>>, row: R) {
    }

    override fun createRow(): TableRow<WrappedRow<R>> = CustomTableRow()

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
