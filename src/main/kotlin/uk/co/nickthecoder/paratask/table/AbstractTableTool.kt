package uk.co.nickthecoder.paratask.table

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.AbstractTool

abstract class AbstractTableTool<R : Any> : AbstractTool(), TableTool<R> {

    override val columns = mutableListOf<Column<R, *>>()

    var list = mutableListOf<R>()

    open val resultsName = "Results"

    abstract fun createColumns()

    override fun createResults(): List<Results> {
        columns.clear()
        createColumns()
        return listOf(TableResults(this, list, resultsName))
    }

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