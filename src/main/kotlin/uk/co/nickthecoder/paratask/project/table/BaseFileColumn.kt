package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.Tooltip
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class BaseFileColumn<R>(
        name: String,
        label: String = name.uncamel(),
        base: File,
        val maxLength: Int = Int.MAX_VALUE,
        getter: (R) -> File) :

        Column<R, File>(name = name, label = label, getter = getter) {

    val prefix = base.path + File.separatorChar

    init {
        setCellFactory { BaseFileTableCell() }
    }

    inner class BaseFileTableCell<R>() : TextFieldTableCell<R, File>() {
        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                var path = item.path
                if (path.startsWith(prefix)) {
                    setText(path.substring(prefix.length))
                }
                if (path.length > maxLength) {
                    path = "…" + path.substring(path.length - maxLength)
                }
                setText(path)
                setTooltip(Tooltip(item.path))
            }
        }
    }
}
