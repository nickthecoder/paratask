package uk.co.nickthecoder.paratask.project.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

class Column<R, T>(
        val name: String,
        override val label: String = name.uncamel(),
        val getter: (R) -> T)

    : TableColumn<WrappedRow<R>, Any>(label), Labelled {

    init {
        setCellValueFactory { p -> p.getValue().observable(name, getter) }
        setEditable(false)
    }

}


