/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.table

import javafx.event.Event
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.util.RequestFocus
import java.lang.ref.WeakReference

class EditCell<S, T>(
        tableResults: TableResults<*>,
        val converter: StringConverter<T>)

    : TableCell<S, T>() {

    // In order to prevent/hunt down memory leaks, I've made this a weak reference
    val tableResultsRef = WeakReference(tableResults)

    // Text field for editing
    private val textField = TextField()

    init {
        create()
    }

    fun create() {
        textField.contextMenu = tableResultsRef.get()?.contextMenu
        textField.styleClass.add("edit-cell")
        itemProperty().addListener { _, _, newItem ->
            if (newItem == null) {
                setText(null)
            } else {
                setText(converter.toString(newItem))
            }
        }
        graphic = textField
        contentDisplay = ContentDisplay.TEXT_ONLY

        textField.setOnAction { _ ->
            commitEdit(this.converter.fromString(textField.text))
        }

        textField.focusedProperty().addListener { _, _, isNowFocused ->
            ParaTaskApp.logFocus("EditCell focusPropertyChanged to $isNowFocused")
            if (!isNowFocused) {
                ParaTaskApp.logFocus("EditCell focusPropertyChanged. commitEdit")
                commitEdit(this.converter.fromString(textField.text))
            }
        }

        textField.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            if (ParataskActions.ESCAPE.match(event)) {
                // I don't think this EVER gets called. Hmmm.
                textField.text = converter.toString(item)
                cancelEdit()
                event.consume()
            } else if (ParataskActions.CONTEXT_MENU.match(event)) {
                tableResultsRef.get()?.showContextMenu(textField, event)
                event.consume()
            } else if (ParataskActions.SELECT_ROW_DOWN.match(event) || ParataskActions.SELECT_ROW_UP.match(event)) {
                // Save the code, so that TableResults can copy the code into the next/prev row.
                saveCode()
            }
        }

        textField.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
        textField.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }

    }

    fun onMouse(event: MouseEvent) {
        if (event.isPopupTrigger) {
            tableResultsRef.get()?.showContextMenu(textField, event)
            tableResultsRef.get()?.contextMenu?.show(textField, Side.LEFT, event.x, event.y)
            event.consume()
        }
    }

    // set the text of the text field and display the graphic
    override fun startEdit() {
        super.startEdit()
        textField.text = converter.toString(item)
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        ParaTaskApp.logFocus("EditCell startEdit. RequestFocus.requestFocus(textField)")
        RequestFocus.requestFocus(textField)
    }

    // revert to text display
    override fun cancelEdit() {
        super.cancelEdit()
        contentDisplay = ContentDisplay.TEXT_ONLY
    }

    fun saveCode() {
        (tableView.items[tableRow.index] as WrappedRow<*>).code = textField.text
    }

    // commits the edit. Update property if possible and revert to text display
    override fun commitEdit(item: T) {

        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing && item != getItem()) {
            val table = tableView
            if (table != null) {
                val column = tableColumn
                val event = CellEditEvent<S, T>(table,
                        TablePosition<S, T>(table, index, column),
                        TableColumn.editCommitEvent(), item)

                Event.fireEvent(column, event)
            }
        }
        super.commitEdit(item)

        contentDisplay = ContentDisplay.TEXT_ONLY
    }

}

/**
 * Convenience converter that does nothing (converts Strings to themselves and vice-versa...).
 */
class IdentityConverter : StringConverter<String>() {

    override fun toString(obj: String?): String? = obj

    override fun fromString(string: String?): String? = string
}