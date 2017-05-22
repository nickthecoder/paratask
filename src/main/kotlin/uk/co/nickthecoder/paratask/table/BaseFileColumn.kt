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

class BaseFileColumn<R>(
        name: String,
        base: File,
        width: Int? = null,
        label: String = name.uncamel(),
        getter: (R) -> File) :

        Column<R, File>(name = name, label = label, width = width, getter = getter) {

    val prefix = base.path + File.separatorChar

    init {
        setCellFactory { BaseFileTableCell() }
        styleClass.add("path")

    }

    inner class BaseFileTableCell<R> : TextFieldTableCell<R, File>() {
        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                val path = item.path
                if (path.startsWith(prefix)) {
                    setText(path.substring(prefix.length))
                } else {
                    setText(path)
                }
                setTooltip(Tooltip(path))
            }
        }
    }
}
