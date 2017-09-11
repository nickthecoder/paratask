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
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

open class TimestampColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> Long) :

        Column<R, Long>(
                name = name,
                label = label,
                getter = getter,
                width = 100) {

    override val filterGetter = { row: R ->
        LocalDateTime.ofInstant(Instant.ofEpochMilli(getter(row)), ZoneId.systemDefault())
    }

    init {
        setCellFactory { DateTableCell() }
        styleClass.add("timestamp")
    }


    class DateTableCell<R> : TextFieldTableCell<R, Long>() {
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
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val shortDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM")

        fun format(millis: Long): String {
            val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            val now = LocalDateTime.now()
            val duration = Duration.between(date, now)
            val days = duration.toDays()
            if (days > 365) {
                return dateFormat.format(date)
            }
            if (days > 7) {
                return shortDateFormat.format(date)
            }
            if (days > 1) {
                return "$days days ago"
            }
            val hours = duration.toHours()
            if (hours > 1) {
                return "$hours hours ago"
            }
            return "${duration.toMinutes()} minutes ago"
        }
    }
}