package uk.co.nickthecoder.paratask.table

import javafx.scene.control.Tooltip
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class BaseFileColumn<R>(
        name: String,
        base: File,
        width: Int? = null,
        label: String = name.uncamel(),
        getter: (R) -> File) :

        Column<R, File>(name = name, label = label, width = width, getter = getter) {

    val prefix = base.path + File.separatorChar

    init {
        setCellFactory { BaseFileTableCell() }
        getStyleClass().add("path")

    }

    inner class BaseFileTableCell<R>() : TextFieldTableCell<R, File>() {
        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                val path = item.path
                if (path.startsWith(prefix)) {
                    setText(path.substring(prefix.length))
                } else {
                    setText(path)
                }
                setTooltip(Tooltip(path))
            }
        }
    }
}
