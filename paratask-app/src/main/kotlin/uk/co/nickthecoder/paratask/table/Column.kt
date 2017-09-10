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

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

open class Column<R, T>(
        val name: String,
        width: Int? = null,
        override val label: String = name.uncamel(),
        val getter: (R) -> T

) : TableColumn<WrappedRow<R>, T>(label), Labelled {

    /**
     * Used by RowFilter, for example DirectoryTool's "name" column is for a column of type File, but this getter will
     * return a String (from file.name)
     */
    open val filterGetter: (R) -> Any? = getter

    init {
        @Suppress("UNCHECKED_CAST")
        setCellValueFactory { p -> p.value.observable(name, getter) as ObservableValue<T> }
        isEditable = false
        if (width != null) {
            prefWidth = width.toDouble()
        }
    }
}
