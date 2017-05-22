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

import com.sun.javafx.collections.ImmutableObservableList
import javafx.application.Platform
import javafx.collections.transformation.SortedList
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import uk.co.nickthecoder.paratask.project.Actions
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.options.Option
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.options.RowOptionsRunner

open class TableResults<R : Any>(final override val tool: TableTool<R>, val list: List<R>, label: String = "Results") :

        AbstractResults(tool, label) {

    val data = WrappedList(list)

    val tableView: TableView<WrappedRow<R>> = TableView()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn("")

    val runner = RowOptionsRunner<R>(tool)

    override fun attached(toolPane: ToolPane) {

        with(codeColumn) {
            setCellValueFactory { p -> p.value.codeProperty }
            isEditable = true
            setCellFactory({ EditCell(this@TableResults, IdentityConverter()) })
            prefWidth = 50.0
        }
        tableView.columns.add(codeColumn)

        for (column in tool.columns) {
            tableView.columns.add(column)
        }

        val sortedList = SortedList(data)
        sortedList.comparatorProperty().bind(tableView.comparatorProperty())

        with(tableView) {
            items = sortedList
            isEditable = true
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }

            tableView.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
            rowFactory = Callback { tool.createRow() }
        }
    }

    override fun selected() {
        tableView.requestFocus()
    }

    override fun detaching() {}

    override fun focus() {
        tableView.requestFocus()
    }

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
                        runner.runDefault(tableView.items[rowIndex].row)
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

            val batchOptions = mutableMapOf<Option, MutableList<WrappedRow<R>>>()

            for (wrappedRow in tableView.items) {
                val code = wrappedRow.code
                if (code != "") {

                    val option = OptionsManager.findOption(code, tool.optionsName)
                    if (option != null) {

                        var list = batchOptions[option]
                        if (list == null) {
                            list = mutableListOf<WrappedRow<R>>()
                            batchOptions.put(option, list)
                        }
                        list.add(wrappedRow)

                        wrappedRow.clearOption()
                    }
                }
            }

            if (batchOptions.isNotEmpty()) {
                runner.runBatch(batchOptions, newTab = newTab, prompt = prompt)
            } else {
                val rowIndex = tableView.selectionModel.focusedIndex
                if (rowIndex >= 0) {
                    runner.runDefault(tableView.items[rowIndex].row, newTab = newTab)
                }

            }
        }
    }

}


class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    val list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
