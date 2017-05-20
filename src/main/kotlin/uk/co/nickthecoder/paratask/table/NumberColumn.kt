package uk.co.nickthecoder.paratask.table

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