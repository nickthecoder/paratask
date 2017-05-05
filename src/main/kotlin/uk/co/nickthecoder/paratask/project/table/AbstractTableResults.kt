package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination.ModifierValue
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.option.OptionRunner

// TODO Get the shortcuts from Shortcuts
val acceleratorRun = KeyCodeCombination(KeyCode.ENTER)
//val acceleratorRunNewTab = KeyCodeCombination(KeyCode.ENTER, control = ModifierValue.DOWN)

val acceleratorDown = KeyCodeCombination(KeyCode.DOWN)
val acceleratorUp = KeyCodeCombination(KeyCode.UP)
val acceleratorEscape = KeyCodeCombination(KeyCode.ESCAPE)

abstract class AbstractTableResults<R>(val tool: Tool, val list: List<R>) : TableResults<R> {

    val data = WrappedList<R>(list)

    override val tableView: TableView<WrappedRow<R>> = TableView()

    override val columns = mutableListOf<Column<R, *>>()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn<WrappedRow<R>, String>("")

    val runner = OptionRunner(tool)

    override fun attached(toolPane: ToolPane) {

        with(codeColumn) {
            setCellValueFactory { p -> p.getValue().codeProperty }
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
            val rowIndex = tableView.selectionModel.focusedIndex
            if (rowIndex >= 0) {
                when (event.clickCount) {
                    1 -> { // Edit the row's option field
                        tableView.edit(rowIndex, codeColumn)
                    }
                    2 -> {
                        runner.runDefault(tableView.items[rowIndex])
                    }
                }
            }
        }
    }

    open fun onKeyPressed(event: KeyEvent) {
        if (acceleratorUp.match(event)) {
            // If we consume then EditCell doesn't get this event. Hmmm.
            //event.consume()
            move(-1)

        } else if (acceleratorDown.match(event)) {
            //event.consume()
            move(1)

        } else if (acceleratorEnter.match(event)) {
            runTableOptions()
            event.consume()

        } else if (acceleratorEscape.match(event)) {
            //println("ATR Escape")
            event.consume()
        }
    }

    fun move(delta: Int) {
        val row = tableView.selectionModel.focusedIndex + delta
        if (row < 0 || row >= tableView.items.size) {
            //println("ATR. out of bounds. ignoring") // TODO Remove
            return
        }
        //tableView.selectionModel.focus(row)
        //println("ATR. editing a different row") // TODO Remove
        Platform.runLater { tableView.edit(tableView.selectionModel.focusedIndex, codeColumn) }
    }

    fun runTableOptions() {
        for (wrappedRow in tableView.items) {
            val code = wrappedRow.code
            if (code != "") {
                val row = wrappedRow.row!!
                // TODO Complete.
                // Build a list of things to run
                // Later I'll also have a map? of isMultiple options, and add rows as we find them
                // Check if a new tab is requested twice. 
            }
        }

        // If no options were typed, then run the default option for the current row
        val rowIndex = tableView.selectionModel.focusedIndex
        if (rowIndex >= 0) {
            runner.runDefault(tableView.items[rowIndex])
        }
    }
}

class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
