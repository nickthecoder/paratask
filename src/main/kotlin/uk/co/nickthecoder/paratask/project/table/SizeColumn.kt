package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.math.BigDecimal
import java.text.DecimalFormat

open class SizeColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> Long) :

        Column<R, Long>(name = name, label = label, getter = getter) {

    init {
        setCellFactory { SizeTableCell() }
        getStyleClass().add("size")

    }

    class SizeTableCell<R>() : TextFieldTableCell<R, Long>() {
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
        val units = listOf<String>("bytes", "kB", "MB", "GB", "TB", "PB")

        val format = DecimalFormat("#,###.0")

        fun format(size: Long): String {
            val limit = BigDecimal(999)
            val scale = BigDecimal(1000)
            var i = 0
            var value = BigDecimal(size)
            while (value > limit) {
                value = value / scale
                i++
            }
            return format.format(value) + units[i]
        }
    }
}