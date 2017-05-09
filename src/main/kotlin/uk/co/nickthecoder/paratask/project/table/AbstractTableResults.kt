package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.collections.transformation.SortedList
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import uk.co.nickthecoder.paratask.gui.Actions
import uk.co.nickthecoder.paratask.gui.project.AbstractResults
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.option.Option
import uk.co.nickthecoder.paratask.project.option.OptionsManager
import uk.co.nickthecoder.paratask.project.option.RowOptionsRunner

abstract class AbstractTableResults<R : Any>(val tool: Tool, val list: List<R>, label: String = "Results") :

        AbstractResults(label), TableResults<R> {

    val data = WrappedList<R>(list)

    override val tableView: TableView<WrappedRow<R>> = TableView()

    override val columns = mutableListOf<Column<R, *>>()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn<WrappedRow<R>, String>("")

    val runner = RowOptionsRunner<R>(tool)

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

        val sortedList = SortedList(data)
        sortedList.comparatorProperty().bind(tableView.comparatorProperty())

        with(tableView) {
            setItems(sortedList)
            setEditable(true)
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, { event ->
                onMouseClick(event)
            })
            tableView.addEventFilter(KeyEvent.KEY_PRESSED, { event ->
                onKeyPressed(event)
            })
            rowFactory = Callback { createRow() }
        }
    }

    fun createRow(): TableRow<WrappedRow<R>> = CustomTableRow()

    inner class CustomTableRow() : TableRow<WrappedRow<R>>() {
        override fun updateItem(wrappedRow: WrappedRow<R>?, empty: Boolean) {
            super.updateItem(wrappedRow, empty)
            if (!empty && wrappedRow != null) {
                updateRow(this, wrappedRow.row)
            }
        }
    }

    open fun updateRow(tableRow: CustomTableRow, row: R) {
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
                        runner.runDefault(tableView.items[rowIndex].row)
                    }
                }
            }
        }
    }

    open fun onKeyPressed(event: KeyEvent) {
        if (Actions.acceleratorUp.match(event)) {
            // If we consume then EditCell doesn't get this event. Hmmm.
            //event.consume()
            move(-1)

        } else if (Actions.acceleratorDown.match(event)) {
            //event.consume()
            move(1)

        } else if (Actions.OPTION_RUN.match(event)) {
            runTableOptions(newTab = event.isShiftDown)
            event.consume()

        } else if (Actions.OPTION_PROMPT.match(event)) {
            runTableOptions(newTab = event.isShiftDown)
            event.consume()

        } else if (Actions.acceleratorEscape.match(event)) {
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
            val multipleOptions = mutableMapOf<Option, MutableList<R>>()

            var foundCode: Boolean = false

            for (wrappedRow in tableView.items) {
                val code = wrappedRow.code
                if (code != "") {
                    foundCode = true

                    val option = OptionsManager.findOption(code, tool.optionsName)
                    if (option != null) {

                        if (option.isMultiple) {
                            var list = multipleOptions.get(option)
                            if (list == null) {
                                list = mutableListOf<R>()
                                multipleOptions.put(option, list)
                            }
                            list.add(wrappedRow.row)

                        } else {
                            singleOptions.add(SingleRowOption(option, wrappedRow.row))
                        }
                        wrappedRow.clearOption()

                    } else {
                        println("Didn't find code $code in $tool.optionsName")
                    }
                }
            }

            if (foundCode) {
                for ((option, list) in multipleOptions) {
                    runner.runMultiple(option, list, newTab = newTab, prompt = prompt)
                }
                for (sr in singleOptions) {
                    if (sr.option.isRow) {
                        runner.runRow(sr.option, sr.row, newTab = newTab, prompt = prompt)
                    } else {
                        runner.runNonRow(sr.option, newTab = newTab, prompt = prompt)
                    }
                }
            } else {
                val rowIndex = tableView.selectionModel.focusedIndex
                if (rowIndex >= 0) {
                    runner.runDefault(tableView.items[rowIndex].row, newTab = newTab)
                }

            }
        }
    }


    private inner class SingleRowOption(val option: Option, val row: R) {
    }
}


class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
