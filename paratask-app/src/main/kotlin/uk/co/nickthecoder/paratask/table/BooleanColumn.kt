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

import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel

open class BooleanColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> Boolean) :

        Column<R, Boolean>(name = name, label = label, getter = getter) {

    init {
        setCellFactory { BooleanTableCell() }
        styleClass.add("boolean")
    }

    class BooleanTableCell<R> : TextFieldTableCell<R, Boolean>() {
        override fun updateItem(item: Boolean?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty) {
                setText(null)
            } else {
                setText(when (item) {
                    true -> "✔"
                    false -> null
                    else -> "-"
                })
            }
        }
    }
}