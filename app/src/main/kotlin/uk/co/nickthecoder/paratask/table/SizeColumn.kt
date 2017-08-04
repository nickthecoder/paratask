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
import java.text.DecimalFormat

open class SizeColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> Long) :

        Column<R, Long>(name = name, label = label, getter = getter) {

    init {
        setCellFactory { SizeTableCell() }
        styleClass.add("size")
        styleClass.add("number")
    }

    class SizeTableCell<R> : TextFieldTableCell<R, Long>() {
        override fun updateItem(item: Long?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                setText(format(item))
            }
        }
    }

    companion object {
        private val units = listOf("bytes", "kB", "MB", "GB", "TB", "PB")

        private val format1 = DecimalFormat("#,###.0")
        private val format2 = DecimalFormat("#,###")
        private val maxNoDecimals = 100.0

        fun format(size: Long): String {
            val limit = 999.0
            val scale = 1000.0
            var i = 0
            var value: Double = size.toDouble()
            while (value > limit) {
                value /= scale
                i++
            }
            val format = if (i == 0 || value > maxNoDecimals) format2 else format1
            return format.format(value) + " " + units[i]
        }
    }
}