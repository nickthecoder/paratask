package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.gui.project.ToolPane

val acceleratorEnter = KeyCodeCombination(KeyCode.ENTER)
val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
val acceleratorUp = KeyCodeCombination(KeyCode.UP)
val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)

abstract class AbstractTableResults<R>(val list: List<R>) : TableResults<R> {

    val data = WrappedList<R>(list)

    override val tableView: TableView<WrappedRow<R>> = TableView()

    override val columns = mutableListOf<Column<R, *>>()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn<WrappedRow<R>, String>("")

    override fun attached(toolPane: ToolPane) {

        with(codeColumn) {
            setCellValueFactory { p -> p.getValue().optionProperty }
            setEditable(true)
            setCellFactory({ EditCell(IdentityConverter()) })
        }
        tableView.getColumns().add(codeColumn)

        for (column in columns) {
            tableView.getColumns().add(column)
        }

        with(tableView) {
            setItems(data)
            setEditable(true)
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, { event ->
                onMouseClick(event)
            })
            tableView.addEventFilter(KeyEvent.KEY_PRESSED, { event ->
                onKeyPressed(event)
            })

        }
    }

    override fun detaching() {}

    open fun onMouseClick(event: MouseEvent) {
        if (event.button == MouseButton.PRIMARY) {
            when (event.clickCount) {
                1 -> { // Edit the row's option field
                    val row = tableView.selectionModel.focusedIndex
                    tableView.edit(row, codeColumn)
                }
                2 -> {
                    println("Double click")
                }
            }
        }
    }

    open fun onKeyPressed(event: KeyEvent) {
        if (acceleratorUp.match(event)) {
            event.consume()
            move(-1)

        } else if (acceleratorDown.match(event)) {
            event.consume()
            move(1)

        } else if (acceleratorEnter.match(event)) {
            //println("ATR Enter")
            event.consume()

        } else if (acceleratorEscape.match(event)) {
            //println("ATR Escape")
            event.consume()
        }
    }

    fun move(delta: Int) {
        val row = tableView.selectionModel.focusedIndex + delta
        if (row < 0 || row >= tableView.items.size) {
            return
        }
        //tableView.selectionModel.focus(row)
        Platform.runLater { tableView.edit(tableView.selectionModel.focusedIndex, codeColumn) }
    }
}

class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
