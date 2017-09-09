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

import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import uk.co.nickthecoder.paratask.AbstractTool

abstract class AbstractTableTool<R : Any> : AbstractTool(), TableTool<R> {

    val columns = mutableListOf<Column<R, *>>()

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
