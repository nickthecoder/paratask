package uk.co.nickthecoder.paratask.project.table

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.collections.transformation.SortedList
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
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

open class TableResults<R : Any>(override val tool: TableTool<R>, val list: List<R>, label: String = "Results") :

        AbstractResults(tool, label) {

    val data = WrappedList<R>(list)

    val tableView: TableView<WrappedRow<R>> = TableView()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn<WrappedRow<R>, String>("")

    val runner = RowOptionsRunner<R>(tool)

    override fun attached(toolPane: ToolPane) {

        with(codeColumn) {
            setCellValueFactory { p -> p.getValue().codeProperty }
            setEditable(true)
            setCellFactory({ EditCell(this@TableResults, IdentityConverter()) })
            prefWidth = 50.0
        }
        tableView.getColumns().add(codeColumn)

        for (column in tool.columns) {
            tableView.getColumns().add(column)
        }

        val sortedList = SortedList(data)
        sortedList.comparatorProperty().bind(tableView.comparatorProperty())

        with(tableView) {
            setItems(sortedList)
            setEditable(true)
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }

            tableView.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
            rowFactory = Callback { tool.createRow() }
        }
    }

    override fun focus() {
        tableView.requestFocus()
    }

    override fun detaching() {}

    open fun onMouseClicked(event: MouseEvent) {

        contextMenu.hide()

        val rowIndex = tableView.selectionModel.focusedIndex
        if (rowIndex >= 0) {
            if (event.button == MouseButton.PRIMARY) {
                when (event.clickCount) {
                    1 -> { // Edit the row's option field
                        tableView.edit(rowIndex, codeColumn)
                    }
                    2 -> {
                        runner.runDefault(tableView.items[rowIndex].row, newTab = event.isShiftDown)
                    }
                }

            } else if (event.button == MouseButton.MIDDLE) {
                runner.runDefault(tableView.items[rowIndex].row, newTab = true)

            } else if (event.button == MouseButton.SECONDARY) {
                showContextMenu(tableView, event)
            }
        }
    }

    fun showContextMenu(node: Node, event: Any) {
        val rows = tableView.selectionModel.selectedItems.map { it.row }
        runner.buildContextMenu(contextMenu, rows)
        if (event is MouseEvent) {
            contextMenu.show(node, Side.LEFT, event.x, event.y)
        } else {
            contextMenu.show(node, Side.BOTTOM, 0.0, 0.0)
        }
    }

    val contextMenu = ContextMenu()

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
                        // Didn't find code
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
