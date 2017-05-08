package uk.co.nickthecoder.paratask.project.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

open class Column<R, T>(
        val name: String,
        override val label: String = name.uncamel(),
        val getter: (R) -> T)

    : TableColumn<WrappedRow<R>, T>(label), Labelled {

    init {
        @Suppress("UNCHECKED_CAST")
        setCellValueFactory { p -> p.getValue().observable(name, getter) as ObservableValue<T> }
        setEditable(false)
    }
}


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

