package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.project.AbstractTool

abstract class AbstractTableTool<R : Any>() : AbstractTool(), TableTool<R> {

    override val columns = mutableListOf<Column<R, *>>()

    var list = mutableListOf<R>()

    open val resultsName = "Results"

    abstract fun createColumns()

    override open fun createResults(): List<Results> {
        columns.clear()
        createColumns()
        return listOf<TableResults<R>>(TableResults<R>(this, list, resultsName))
    }

    open fun updateRow(tableRow: TableRow<WrappedRow<R>>, row: R) {
    }

    open override fun createRow(): TableRow<WrappedRow<R>> = CustomTableRow()

    /**
     * Allows rows to be styled. For example, GitTool colours the row based on the state of the file
     */
    inner class CustomTableRow() : TableRow<WrappedRow<R>>() {
        override fun updateItem(wrappedRow: WrappedRow<R>?, empty: Boolean) {
            super.updateItem(wrappedRow, empty)
            if (!empty && wrappedRow != null) {
                updateRow(this, wrappedRow.row)
            }
        }
    }
}