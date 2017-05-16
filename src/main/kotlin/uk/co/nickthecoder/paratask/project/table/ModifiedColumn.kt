package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.text.DateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

open class ModifiedColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> Long) :

        Column<R, Long>(name = name, label = label, getter = getter, width = 100) {

    init {
        setCellFactory { DateTableCell() }
        getStyleClass().add("modified")

    }

    class DateTableCell<R>() : TextFieldTableCell<R, Long>() {
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
        val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val shortDateFormat = DateTimeFormatter.ofPattern("d MMMM")

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
                return "${days} days ago"
            }
            val hours = duration.toHours()
            if (hours > 1) {
                return "${hours} hours ago"
            }
            return "${duration.toMinutes()} minutes ago"
        }
    }
}