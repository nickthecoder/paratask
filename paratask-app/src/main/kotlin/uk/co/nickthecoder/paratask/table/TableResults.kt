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
import com.sun.javafx.scene.control.skin.TableColumnHeader
import com.sun.javafx.scene.control.skin.TableViewSkin
import com.sun.javafx.scene.control.skin.VirtualFlow
import javafx.application.Platform
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.util.Callback
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.DragHelper
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.options.Option
import uk.co.nickthecoder.paratask.options.OptionsManager
import uk.co.nickthecoder.paratask.options.RowOptionsRunner
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.project.ResultsTab
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.filter.RowFilter


open class TableResults<R : Any>(
        final override val tool: TableTool<R>,
        val list: List<R>,
        label: String = "Results",
        val columns: List<Column<R, *>>,
        val rowFilter: RowFilter<R>? = null,
        canClose: Boolean = false) :

        AbstractResults(tool, label, canClose = canClose) {

    val data = WrappedList(list)

    val tableView: TableView<WrappedRow<R>> = TableView()

    override val node = tableView

    private val codeColumn: TableColumn<WrappedRow<R>, String> = TableColumn("")

    val runner = RowOptionsRunner<R>(tool)

    var filteredData: FilteredList<WrappedRow<R>>? = null

    /**
     * Used to ensure that the currently selected row is always visible. See move()
     */
    var virtualFlow: VirtualFlow<*>? = null

    var dropHelper: DropHelper? = null
        set(v) {
            field?.cancel()
            field = v
            v?.applyTo(tableView)
        }

    var dragHelper: DragHelper? = null

    init {
        // Find the VitualFlow as soon as the tableView's skin has been set
        tableView.skinProperty().addListener { _, _, skin ->
            if (skin is TableViewSkin<*>) {
                virtualFlow = skin.children.filterIsInstance<VirtualFlow<*>>().firstOrNull()
            }
        }
    }

    fun selectedRows(): List<R> {
        return tableView.selectionModel.selectedItems.map { it.row }
    }

    override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {

        super.attached(resultsTab, toolPane)
        with(codeColumn) {
            setCellValueFactory { p -> p.value.codeProperty }
            isEditable = true
            setCellFactory({ EditCell(this@TableResults, IdentityConverter()) })
            prefWidth = 50.0
        }
        tableView.columns.add(codeColumn)

        for (column in columns) {
            if (rowFilter?.filtersColumn(column) == true) {
                column.graphic = ImageView(ParaTask.imageResource("buttons/filter.png"))
            } else {
                column.graphic = null
            }
            tableView.columns.add(column)
        }

        val sortedList: SortedList<WrappedRow<R>>
        if (rowFilter == null) {
            sortedList = SortedList(data)
        } else {
            filteredData = FilteredList(data) { rowFilter.accept(it.row) }
            sortedList = SortedList(filteredData)
        }
        sortedList.comparatorProperty().bind(tableView.comparatorProperty())

        with(tableView) {
            items = sortedList
            isEditable = true
            selectionModel.selectionMode = SelectionMode.MULTIPLE

            tableView.addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }
            rowFactory = Callback {
                val tableRow = tool.createRow()
                dragHelper?.applyTo(tableRow)
                tableRow.setOnMouseClicked { onRowClicked(it, tableRow) }
                tableRow
            }
        }
        dropHelper?.applyTo(resultsTab)

        if (rowFilter != null) {
            tableView.addEventFilter(MouseEvent.MOUSE_PRESSED) { if (it.button == MouseButton.SECONDARY) it.consume() }
            tableView.addEventFilter(MouseEvent.MOUSE_RELEASED) { tableMouseEvent(it) }
        }
    }

    override fun detaching() {
        super.detaching()
        dropHelper?.cancel()
    }

    override fun selected() {
        super.selected()
        tool.tabDropHelper = dropHelper
    }

    override fun deselected() {
        stopEditing()
        tool.tabDropHelper = null
        super.deselected()
    }

    fun tableMouseEvent(event: MouseEvent) {
        if (event.button == MouseButton.SECONDARY) {
            event.consume()

            var node: Node? = event.target as Node
            while (node != null && node !== tableView) {
                if (node is TableColumnHeader) {
                    changeColumnFilter(node)
                    return
                }
                node = node.parent
            }
        }
    }

    fun changeColumnFilter(tch: TableColumnHeader) {

        val tchCol = tch.tableColumn
        val column: Column<R, *>? = if (tchCol is Column<*, *>) {
            @Suppress("UNCHECKED_CAST")
            tchCol as Column<R, *>
        } else {
            null
        }

        rowFilter?.editColumnFilters(column) {
            Platform.runLater {
                filteredData?.setPredicate { rowFilter.accept(it.row) }

                columns.forEach { col ->
                    if (rowFilter.filtersColumn(col)) {
                        col.graphic = ImageView(filterIcon)
                    } else {
                        col.graphic = null
                    }
                }
                if (rowFilter.filtersColumn(null)) {
                    codeColumn.graphic = ImageView(filterIcon)
                } else {
                    codeColumn.graphic = null
                }
            }
        }
    }


    fun stopEditing() {
        tableView.edit(-1, null)
    }

    override fun focus() {
        ParaTaskApp.logFocus("TableResults focus. runLater(...)")

        Platform.runLater {
            if (tableView.items.isNotEmpty()) {
                ParaTaskApp.logFocus("TableResults focus. tableView.requestFocus()")

                tableView.requestFocus()
                val index = tableView.selectionModel.focusedIndex
                if (index < 0) {
                    tableView.selectionModel.clearAndSelect(0)
                    ParaTaskApp.logFocus("TableResults focus. tableView.selectionModel.focus(0)")
                    tableView.selectionModel.focus(0)
                    editOption(0)
                } else {
                    tableView.selectionModel.select(index)
                    editOption(index)
                }
            }
        }
    }

    fun editOption(rowIndex: Int = -1) {
        val index = if (rowIndex >= 0) rowIndex else tableView.selectionModel.focusedIndex

        stopEditing()
        tableView.edit(index, codeColumn)
    }

    open fun onRowClicked(event: MouseEvent, tableRow: TableRow<WrappedRow<R>>) {
        contextMenu.hide()
        if (tableRow.item != null) {

            if (event.button == MouseButton.PRIMARY) {
                when (event.clickCount) {
                    1 -> { // Edit the tabelRow's option field
                        editOption(tableRow.index)
                    }
                    2 -> {
                        runner.runDefault(tableRow.item.row)
                    }
                }

            } else if (event.button == MouseButton.MIDDLE) {
                runner.runDefault(tableRow.item.row, newTab = true)

            } else if (event.button == MouseButton.SECONDARY) {
                showContextMenu(tableRow, event)
            }
        }
    }

    fun showContextMenu(node: Node, event: Any) {
        val rows = tableView.selectionModel.selectedItems.map { it.row }
        runner.buildContextMenu(contextMenu, rows)
        if (event is MouseEvent) {
            contextMenu.show(node, Side.BOTTOM, event.x, 0.0)
        } else {
            contextMenu.show(node, Side.BOTTOM, 0.0, 0.0)
        }
    }

    val contextMenu = ContextMenu()

    /**
     * Looks for shortcuts for the row-options, and passes control to super if no row-base options were found.
     * If the option found only matches SOME of the selected rows, then the rows that match will be unselected,
     * leaving the unmatches rows selected. For example, using a shortcut that has different options for files and
     * directories, it will only process one half.
     * The user can then hit the shortcut again to apply the other half.
     */
    override fun checkOptionShortcuts(event: KeyEvent) {

        val tableRows = tableView.selectionModel.selectedItems
        if (tableRows.isEmpty()) {
            return
        }

        val topLevelOptions = OptionsManager.getTopLevelOptions(tool.optionsName)
        topLevelOptions.listFileOptions().forEach { fileOptions ->
            fileOptions.listOptions().forEach { option ->
                if (option.isRow) {
                    option.shortcut?.let { shortcut ->
                        if (shortcut.match(event) && fileOptions.acceptRow(tableRows[0].row)) {
                            val acceptedRows = tableRows.filter { fileOptions.acceptRow(it.row) }
                            tableView.selectionModel.clearSelection()
                            tableRows.filter { !acceptedRows.contains(it) }.forEach {
                                tableView.selectionModel.select(it)
                            }
                            runner.runRows(option, acceptedRows.map { it.row })
                            return
                        }
                    }
                }
            }
        }

        super.checkOptionShortcuts(event)
    }

    open fun onKeyPressed(event: KeyEvent) {
        if (ParataskActions.PREV_ROW.match(event)) {
            if (!move(-1)) event.consume()

        } else if (ParataskActions.NEXT_ROW.match(event)) {
            if (!move(1)) event.consume()

        } else if (ParataskActions.SELECT_ROW_UP.match(event)) {
            if (!move(-1, false)) event.consume()

        } else if (ParataskActions.SELECT_ROW_DOWN.match(event)) {
            if (!move(1, false)) event.consume()

        } else if (ParataskActions.OPTION_RUN.match(event)) {
            runTableOptions()
            event.consume()

        } else if (ParataskActions.OPTION_RUN_NEW_TAB.match(event)) {
            runTableOptions(newTab = true)
            event.consume()

        } else if (ParataskActions.OPTION_PROMPT.match(event)) {
            runTableOptions(prompt = true)
            event.consume()

        } else if (ParataskActions.OPTION_PROMPT_NEW_TAB.match(event)) {
            runTableOptions(prompt = true, newTab = true)
            event.consume()

        } else if (ParataskActions.SELECT_ALL.match(event)) {
            selectAll()
            event.consume()

        } else if (ParataskActions.SELECT_NONE.match(event)) {
            selectNone()
            event.consume()

        } else if (ParataskActions.ESCAPE.match(event)) {
            event.consume()
        }
    }

    fun move(delta: Int, clearSelection: Boolean = true): Boolean {
        val row = tableView.selectionModel.focusedIndex + delta

        if (row < 0 || row >= tableView.items.size) {
            return false
        }

        // We need to run later so the EditCell has a chance to save the textfield to the WrappedRow.code
        Platform.runLater {
            if (clearSelection) {
                tableView.selectionModel.clearSelection()
            } else {
                // Copy the code from the old focused row.
                val code = tableView.items[row - delta].code
                tableView.items[row].code = code
            }
            tableView.selectionModel.select(row)
            ParaTaskApp.logFocus("TableResults move. tableView.selectionMode.focus(row)")
            tableView.selectionModel.focus(row)

            // Ensure the new row is visible
            virtualFlow?.let {
                val first = it.firstVisibleCell.index
                val last = it.lastVisibleCell.index

                if (row < first || row > last) {
                    it.show(row)
                }
            }
            editOption(row)
        }
        return true
    }

    fun selectAll() {
        stopEditing()
        // We need to run later so the EditCell has a chance to save the textfield to the WrappedRow.code
        Platform.runLater {
            val selectedRow = tableView.selectionModel.selectedIndex
            val code = if (selectedRow >= 0 && selectedRow < tableView.items.count()) tableView.items[selectedRow].code else ""
            tableView.selectionModel.selectAll()
            tableView.items.forEach { it.code = code }
            if (selectedRow >= 0 && selectedRow < tableView.items.count()) {
                editOption(selectedRow)
            }
        }

    }

    fun selectNone() {
        stopEditing()
        Platform.runLater {
            val selectedRow = tableView.selectionModel.selectedIndex
            tableView.selectionModel.clearSelection()
            tableView.items.forEach { it.code = "" }
            if (selectedRow >= 0 && selectedRow < tableView.items.count()) {
                editOption(selectedRow)
            }
        }
    }

    fun runTableOptions(newTab: Boolean = false, prompt: Boolean = false) {

        ParaTaskApp.logFocus("TableResults runTableOptions tableView.requestFocus()")
        // Unfocus from the cell being edited allows it to be committed
        tableView.requestFocus()
        Platform.runLater {

            var foundCode = false

            val batchOptions = mutableMapOf<Option, MutableList<WrappedRow<R>>>()

            for (wrappedRow in tableView.items) {
                val code = wrappedRow.code
                if (code != "") {
                    foundCode = true

                    val option = OptionsManager.findOptionForRow(code, tool.optionsName, wrappedRow.row)
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
            }

            if (!foundCode) {
                // Run the "default" option against the current row
                val rowIndex = tableView.selectionModel.focusedIndex
                if (rowIndex >= 0) {
                    runner.runDefault(tableView.items[rowIndex].row, newTab = newTab)
                }

            }
        }
    }


    companion object {
        val filterIcon = ParaTask.imageResource("buttons/filter.png")
    }
}


class WrappedList<R>(list: List<R>) :
        ImmutableObservableList<WrappedRow<R>>() {

    var list = list.map { row: R -> WrappedRow(row) }

    override fun get(index: Int): WrappedRow<R> = list[index]

    override val size = list.size
}
