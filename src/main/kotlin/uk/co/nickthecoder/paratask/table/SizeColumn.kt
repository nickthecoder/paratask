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