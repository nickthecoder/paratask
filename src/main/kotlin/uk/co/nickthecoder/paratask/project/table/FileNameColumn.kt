package uk.co.nickthecoder.paratask.project.table

import javafx.scene.control.Tooltip
import javafx.scene.control.cell.TextFieldTableCell
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileNameColumn<R>(
        name: String,
        label: String = name.uncamel(),
        getter: (R) -> File) :

        Column<R, File>(name = name, label = label, getter = getter) {

    init {
        setCellFactory { FileNameTableCell() }
    }

    inner class FileNameTableCell<R>() : TextFieldTableCell<R, File>() {
        override fun updateItem(item: File?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty || item == null) {
                setText(null)
            } else {
                setText(item.name)
                setTooltip(Tooltip(item.path))
            }
        }
    }
}
