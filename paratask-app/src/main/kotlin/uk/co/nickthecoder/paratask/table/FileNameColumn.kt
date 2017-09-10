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

import javafx.scene.control.Tooltip
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileNameColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> File) :

        Column<R, File>(name = name, label = label, getter = getter) {

    override val filterGetter: (R) -> Any = { getter(it).name }

    init {
        setCellFactory { FileNameTableCell() }
    }

    inner class FileNameTableCell<R> : TextFieldTableCell<R, File>() {
        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                text = item.name
                setTooltip(Tooltip(item.path))
            }
        }
    }
}
