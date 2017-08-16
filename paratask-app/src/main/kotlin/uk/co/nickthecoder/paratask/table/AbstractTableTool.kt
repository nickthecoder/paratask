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

package uk.co.nickthecoder.paratask.table

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.project.Results

abstract class AbstractTableTool<R : Any> : AbstractTool(), TableTool<R> {

    override val columns = mutableListOf<Column<R, *>>()

    var list = mutableListOf<R>()

    open val resultsName = "Results"

    var tableResults: TableResults<R>? = null

    abstract fun createColumns()

    override fun createResults(): List<Results> {
        columns.clear()
        createColumns()
        return listOf(createTableResults())
    }

    open fun createTableResults(): TableResults<R> {
        tableResults = TableResults(this, list, resultsName)
        return tableResults!!
    }

    open fun updateRow(tableRow: TableRow<WrappedRow<R>>, row: R) {
    }

    override fun createRow(): TableRow<WrappedRow<R>> = CustomTableRow()

    override fun selectedRows(): List<R> {
        return tableResults?.tableView?.selectionModel?.selectedItems?.map { it.row } ?: listOf()
    }

    fun findTableRow(event: DragEvent): Pair<R?, TableRow<WrappedRow<R>>?> {
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