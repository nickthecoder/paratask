package uk.co.nickthecoder.paratask.project.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

open class Column<R, T>(
        val name: String,
        width: Int? = null,
        override val label: String = name.uncamel(),
        val getter: (R) -> T

) : TableColumn<WrappedRow<R>, T>(label), Labelled {

    init {
        @Suppress("UNCHECKED_CAST")
        setCellValueFactory { p -> p.getValue().observable(name, getter) as ObservableValue<T> }
        setEditable(false)
        if (width != null) {
            prefWidth = width.toDouble()
        }
    }
}


