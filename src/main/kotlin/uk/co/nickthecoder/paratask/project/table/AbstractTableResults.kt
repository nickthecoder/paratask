package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import javax.swing.CellEditor

val acceleratorEnter = KeyCodeCombination(KeyCode.ENTER)
val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
val acceleratorUp = KeyCodeCombination(KeyCode.UP)
val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)

abstract class AbstractTableResults<R>(val list: List<R>) : TableResults<R> {

    val data = WrappedList<R>(list)

    override val tableView: TableView<WrappedRow<R>> = TableView()

    override val columns = mutableListOf<Column<R, *>>()

    override val node = tableView

    val optionColumn: TableColumn<WrappedRow<R>, String> = TableColumn<WrappedRow<R>, String>("")

    override fun attached(toolPane: ToolPane) {

        with(optionColumn) {
            setCellValueFactory { p -> p.getValue().optionProperty }
            setEditable(true)
            setCellFactory({ EditCell(IdentityConverter()) })
            // setCellFactory(TextFieldTableCell.forTableColumn())

        }
        tableView.getColumns().add(optionColumn)

        for (column in columns) {
            tableView.getColumns().add(column)
        }

        with(tableView) {
            setItems(data)
            setEditable(true)
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, { event ->
                onClicked()
            })
            tableView.addEventFilter(KeyEvent.KEY_PRESSED, { event ->
                onKeyPressed(event)
            })

        }
    }

    override fun detaching() {}

    open fun onClicked() {
        val row = tableView.selectionModel.focusedIndex
        tableView.edit(row, optionColumn)
    }

    open fun onKeyPressed(event: KeyEvent) {
        if (acceleratorUp.match(event)) {
            //event.consume()
            move(-1)

        } else if (acceleratorDown.match(event)) {
            //event.consume()
            move(1)

        } else if (acceleratorEnter.match(event)) {
            println("ATR Enter")
            event.consume()

        } else if (acceleratorEscape.match(event)) {
            println("ATR Escape")
            event.consume()
        }
    }

    fun move(delta: Int) {
        val row = tableView.selectionModel.focusedIndex + delta
        if (row < 0 || row >= tableView.items.size) {
            return
        }
        //tableView.selectionModel.focus(row)
        Platform.runLater { tableView.edit(tableView.selectionModel.focusedIndex, optionColumn) }
    }
}

class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
