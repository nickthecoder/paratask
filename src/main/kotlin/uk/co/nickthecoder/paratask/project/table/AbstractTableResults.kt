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
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.option.Option
import uk.co.nickthecoder.paratask.project.option.OptionRunner
import uk.co.nickthecoder.paratask.project.option.OptionsManager

// TODO Get the shortcuts from Shortcuts
val acceleratorRun = Actions.OPTIONS_RUN.keyCodeCombination
val acceleratorRunNewTab = Actions.OPTIONS_RUN_NEW_TAB.keyCodeCombination

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

        } else if (acceleratorRun?.match(event) == true) {
            runTableOptions()
            event.consume()

        } else if (acceleratorRunNewTab?.match(event) == true) {
            runTableOptions(newTab = true)
            event.consume()

        } else if (acceleratorEscape.match(event)) {
            event.consume()
        }
    }

    fun move(delta: Int) {
        val row = tableView.selectionModel.focusedIndex + delta
        if (row < 0 || row >= tableView.items.size) {
            return
        }
        Platform.runLater { tableView.edit(tableView.selectionModel.focusedIndex, codeColumn) }
    }

    fun runTableOptions(newTab: Boolean = false, prompt: Boolean = false) {

        // Unfocus from the cell being edited allows it to be committed
        tableView.requestFocus()
        Platform.runLater {

            val singleOptions = mutableListOf<SingleRowOption>()
            var foundCode: Boolean = false

            for (wrappedRow in tableView.items) {
                val code = wrappedRow.code
                if (code != "") {
                    foundCode = true

                    val option = OptionsManager.findOption(code, tool.optionsName)
                    if (option != null) {
                        // TODO Handle multiple row options.
                        singleOptions.add(SingleRowOption(option, wrappedRow))
                        wrappedRow.clearOption()
                    } else {
                        println("Didn't find code $code in $tool.optionsName")
                    }
                }
            }

            // If no options were typed, then run the default option for the current row
            // TODO And multipleOptions.size == 0
            if (foundCode) {
                for (sr in singleOptions) {
                    if (sr.option.isRow) {
                        println("Running row option ${sr.option}")
                        runner.runRow(sr.option, sr.wrappedRow, newTab = newTab, prompt = prompt)
                    } else {
                        println("Running non-row option  ${sr.option}")
                        runner.runNonRow(sr.option, newTab = newTab, prompt = prompt)
                    }
                }
            } else {
                println("No codes found, running default option on current row")
                val rowIndex = tableView.selectionModel.focusedIndex
                if (rowIndex >= 0) {
                    runner.runDefault(tableView.items[rowIndex], newTab = newTab)
                }

                // TODO Run the multi-rows 
            }
        }
    }


}


private data class SingleRowOption(val option: Option, val wrappedRow: WrappedRow<*>) {
}

class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
