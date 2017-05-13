package uk.co.nickthecoder.paratask.project.table

import javafx.application.Platform
import javafx.event.Event
import javafx.geometry.Side
import javafx.scene.control.ContentDisplay
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableColumn.CellEditEvent
import javafx.scene.control.TablePosition
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.Actions

public class EditCell<S, T>(
        val tableResults: AbstractTableResults<*>,
        val converter: StringConverter<T>)

    : TableCell<S, T>() {

    // Text field for editing
    private val textField = TextField()

    init {
        textField.setContextMenu(tableResults.contextMenu)
        textField.styleClass.add("edit-cell")
        itemProperty().addListener { _, _, newItem ->
            if (newItem == null) {
                setText(null)
            } else {
                setText(converter.toString(newItem))
            }
        }
        setGraphic(textField)
        setContentDisplay(ContentDisplay.TEXT_ONLY)

        textField.setOnAction { _ ->
            commitEdit(this.converter.fromString(textField.getText()))
        }

        textField.focusedProperty().addListener { _, _, isNowFocused ->
            if (!isNowFocused) {
                commitEdit(this.converter.fromString(textField.getText()))
            }
        }

        textField.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            if (Actions.acceleratorEscape.match(event)) {
                // I don't think this EVER gets called. Hmmm.
                textField.setText(converter.toString(getItem()))
                cancelEdit()
                event.consume()
            } else if (Actions.acceleratorUp.match(event)) {
                event.consume()
                move(-1)
            } else if (Actions.acceleratorDown.match(event)) {
                event.consume()
                move(1)
            } else if (Actions.CONTEXT_MENU.match(event)) {
                tableResults.showContextMenu( textField, event )
                event.consume()
            }
        }

        textField.addEventHandler(MouseEvent.MOUSE_PRESSED) { onMouse(it) }
        textField.addEventHandler(MouseEvent.MOUSE_RELEASED) { onMouse(it) }

    }

    fun onMouse(event: MouseEvent) {
        if (event.isPopupTrigger) {
            tableResults.showContextMenu( textField, event )
            tableResults.contextMenu.show(textField, Side.LEFT, event.x, event.y)
            event.consume()
        }
    }

    fun move(delta: Int) {
        commitEdit(converter.fromString(textField.getText()))

        val row = tableView.selectionModel.focusedIndex + delta
        if (row < 0 || row >= tableView.items.size) {
            return
        }

        tableView.selectionModel.focus(row)
        Platform.runLater { tableView.edit(row, tableView.columns.get(0)) }

    }

    // set the text of the text field and display the graphic
    override fun startEdit() {
        super.startEdit()
        textField.setText(converter.toString(getItem()))
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
        textField.requestFocus()
    }

    // revert to text display
    override fun cancelEdit() {
        super.cancelEdit()
        setContentDisplay(ContentDisplay.TEXT_ONLY)
    }

    // commits the edit. Update property if possible and revert to text display
    override fun commitEdit(item: T) {

        // This block is necessary to support commit on losing focus, because the baked-in mechanism
        // sets our editing state to false before we can intercept the loss of focus.
        // The default commitEdit(...) method simply bails if we are not editing...
        if (!isEditing() && item != getItem()) {
            val table = getTableView()
            if (table != null) {
                val column = getTableColumn()
                val event = CellEditEvent<S, T>(table,
                        TablePosition<S, T>(table, getIndex(), column),
                        TableColumn.editCommitEvent(), item)

                Event.fireEvent(column, event)
            }
        }

        super.commitEdit(item)

        setContentDisplay(ContentDisplay.TEXT_ONLY)
    }

}

/**
 * Convenience converter that does nothing (converts Strings to themselves and vice-versa...).
 */
class IdentityConverter : StringConverter<String>() {

    override fun toString(obj: String?): String? = obj

    override fun fromString(string: String?): String? = string
}