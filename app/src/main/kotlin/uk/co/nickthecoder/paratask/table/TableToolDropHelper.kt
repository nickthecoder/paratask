package uk.co.nickthecoder.paratask.table

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.control.TableView
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.project.ToolPane

/**
 * A helper class for table tools which can be a drop target for files.
 * The files can either be dropped onto the tab, or onto the table (i.e. this class has TWO DropHelper instances).
 * When dropping onto the table, you can distiguish between dropping to the table as a whole,
 * or dropping to a single row of the table.
 * R is the type or Row
 */
abstract class TableToolDropHelper<T, R : Any>(
        val dataFormat: DataFormat,
        val tool: AbstractTableTool<R>,
        val modes: Array<TransferMode> = TransferMode.ANY) {

    var dropHelperOnTab: DropHelperOnTab? = null

    var dropHelperOnTable: DropHelperOnTable? = null

    var table: TableView<WrappedRow<R>>? = null
        set(v) {
            v?.let {
                dropHelperOnTable = DropHelperOnTable(v, modes = modes) { event ->
                    droppedFilesOnTable(event)
                }
            }
        }

    fun attached(toolPane: ToolPane) {
        val halfTab = toolPane.halfTab
        if (halfTab.isLeft()) {
            dropHelperOnTab = DropHelperOnTab(halfTab.projectTab as Node, modes = modes) { event ->
                droppedFilesOnNonRow(dropHelperOnTab!!.content(event), event.transferMode)
            }
        }
    }

    fun detaching() {
        dropHelperOnTab?.let {
            it.cancel()
            dropHelperOnTab = null
        }
    }

    fun droppedFilesOnTable(event: DragEvent): Boolean {

        val (row, _) = tool.findTableRow(event)
        if (row != null && acceptDropOnRow(row) != null) {
            return droppedFilesOnRow(row, dropHelperOnTable!!.content(event), event.transferMode)
        }
        if (acceptDropOnNonRow() != null) {
            return droppedFilesOnNonRow(dropHelperOnTable!!.content(event), event.transferMode)
        }
        return false
    }

    open fun acceptDropOnNonRow(): Array<TransferMode>? = modes

    open fun acceptDropOnRow(row: R): Array<TransferMode>? = modes

    abstract fun droppedFilesOnRow(row: R, content: T, transferMode: TransferMode): Boolean

    abstract fun droppedFilesOnNonRow(content: T, transferMode: TransferMode): Boolean

    inner class DropHelperOnTab(target: Node,
                                source: Node = target,
                                modes: Array<TransferMode> = TransferMode.ANY,
                                dropped: (DragEvent) -> Boolean)
        : DropHelper<T>(dataFormat, target, source, modes, dropped) {

        override fun accept(event: DragEvent): Array<TransferMode>? {
            return acceptDropOnNonRow()
        }
    }

    /**
     * Style a ROW when dragging files to a directory, otherwise, style the table as a whole
     */
    inner class DropHelperOnTable(target: Node,
                                  source: Node = target,
                                  modes: Array<TransferMode> = TransferMode.ANY,
                                  dropped: (DragEvent) -> Boolean)
        : DropHelper<T>(dataFormat, target, source, modes, dropped) {

        override fun styleableNode(event: DragEvent): Styleable? {
            val (row, tableRow) = tool.findTableRow(event)
            if (row != null && acceptDropOnRow(row) != null) {
                return tableRow
            }

            if (acceptDropOnNonRow() != null) {
                return super.styleableNode(event)
            }

            return null
        }

        override fun accept(event: DragEvent): Array<TransferMode>? {
            val pair = tool.findTableRow(event)
            val row = pair.first
            if (row != null) {
                acceptDropOnRow(row)?.let { return it }
            }

            if (event.gestureSource === source) {
                return null
            }

            return acceptDropOnNonRow()
        }
    }
}