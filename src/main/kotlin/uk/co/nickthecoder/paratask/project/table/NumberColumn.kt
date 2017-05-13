package uk.co.nickthecoder.paratask.project.table

import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel

open class NumberColumn<R,T>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> T) :

        Column<R, T>(name = name, label = label, getter = getter) {

    init {
        getStyleClass().add("number")
    }
}