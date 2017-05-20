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
        getStyleClass().add("boolean")
    }

    class BooleanTableCell<R>() : TextFieldTableCell<R, Boolean>() {
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