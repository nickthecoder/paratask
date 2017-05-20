package uk.co.nickthecoder.paratask.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
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
        setCellValueFactory { p -> p.value.observable(name, getter) as ObservableValue<T> }
        isEditable = false
        if (width != null) {
            prefWidth = width.toDouble()
        }
    }
}


