package uk.co.nickthecoder.paratask.table

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.input.DataFormat
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.CompoundDropHelper
import uk.co.nickthecoder.paratask.gui.DragHelper
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.project.ToolPane

class CompoundToolDropHelper<R : Any>(val tool: ListTableTool<R>, vararg helpers: TableToolDropHelper<*, R>) {

    val dropHelpers: List<TableToolDropHelper<*, R>> = helpers.toList()

    var dropHelpersOnTab = CompoundDropHelper()

    var dropHelpersOnTable = CompoundDropHelper()

    fun attachTableResults(tableResults: TableResults<R>) {
        dropHelpers.forEach {
            dropHelpersOnTable.dropHelpers.add(it.createHelperOnTable())
        }
        dropHelpersOnTable.applyTo(tableResults.tableView)
    }

    fun attachToolPane(toolPane: ToolPane) {
        val halfTab = toolPane.halfTab
        if (halfTab.isLeft()) {
            dropHelpers.forEach {
                dropHelpersOnTab.dropHelpers.add(it.createHelperOnTab())
            }
            dropHelpersOnTab.applyTo(halfTab.projectTab as Node)
        }
    }

    fun detaching() {
        dropHelpersOnTab.cancel()
        dropHelpersOnTable.cancel()
    }
}

/**
 * A helper class for table tools which can be a drop target for files.
 * The files can either be dropped onto the tab, or onto the table (i.e. this class has TWO DropHelper instances).
 * When dropping onto the table, you can distinguish between dropping to the table as a whole,
 * or dropping to a single row of the table.
 * T is the type of data send during drag and drop (it must be Serializable)
 * R is the type or Row
 */
abstract class TableToolDropHelper<T, R : Any>(
        val dataFormat: DataFormat,
        val tool: TableTool<R>,
        allowCopy: Boolean = true,
        allowMove: Boolean = true,
        allowLink: Boolean = true) {

    val modes = DragHelper.modes(allowCopy, allowMove, allowLink)

    var dropHelperOnTab: DropHelperOnTab<T>? = null

    var dropHelperOnTable: DropHelperOnTable<T, R>? = null

    fun createHelperOnTable() = DropHelperOnTable(this, modes = modes)

    fun createHelperOnTab() = DropHelperOnTab(this, modes = modes)

    fun attachTableResults(tableResults: TableResults<R>) {
        dropHelperOnTable?.cancel()

        dropHelperOnTable = createHelperOnTable()
        dropHelperOnTable!!.applyTo(tableResults.tableView)
    }

    fun attachToolPane(toolPane: ToolPane) {
        val halfTab = toolPane.halfTab
        if (halfTab.isLeft()) {
            dropHelperOnTab = createHelperOnTab()
            dropHelperOnTab!!.applyTo(halfTab.projectTab as Node)
        }
    }

    fun detatchin() {
        dropHelperOnTable?.cancel()
        dropHelperOnTab?.cancel()
    }

    fun detaching() {
        dropHelperOnTab?.let {
            it.cancel()
            dropHelperOnTab = null
        }
        dropHelperOnTable?.let {
            it.cancel()
            dropHelperOnTable = null
        }
    }

    open fun acceptDropOnNonRow(): Array<TransferMode>? = modes

    open fun acceptDropOnRow(row: R): Array<TransferMode>? = acceptDropOnNonRow()

    open fun droppedFilesOnRow(row: R, content: T, transferMode: TransferMode): Boolean {
        return droppedFilesOnNonRow(content, transferMode)
    }

    abstract fun droppedFilesOnNonRow(content: T, transferMode: TransferMode): Boolean

}


class DropHelperOnTab<T>(
        val parent: TableToolDropHelper<T, *>,
        modes: Array<TransferMode> = TransferMode.ANY)

    : DropHelper<T>(parent.dataFormat, modes) {

    override fun accept(event: DragEvent): Array<TransferMode>? {
        return parent.acceptDropOnNonRow()
    }

    override fun onDropped(event: DragEvent): Boolean {
        val content = content(event)
        if (content != null) {
            return parent.droppedFilesOnNonRow(content, event.transferMode)
        }
        return false
    }
}


/**
 * Style a ROW when dragging files to a directory, otherwise, style the table as a whole
 */
class DropHelperOnTable<T, R : Any>(
        val parent: TableToolDropHelper<T, R>,
        modes: Array<TransferMode> = TransferMode.ANY)

    : DropHelper<T>(parent.dataFormat, modes = modes) {

    override fun styleableNode(event: DragEvent): Styleable? {
        val (row, tableRow) = parent.tool.findTableRow(event)
        if (row != null && parent.acceptDropOnRow(row) != null) {
            return tableRow
        }

        if (parent.acceptDropOnNonRow() != null) {
            return super.styleableNode(event)
        }

        return null
    }

    override fun onDropped(event: DragEvent): Boolean {

        val content = content(event)
        content ?: return false

        val (row, _) = parent.tool.findTableRow(event)
        if (row != null && parent.acceptDropOnRow(row) != null) {
            return parent.droppedFilesOnRow(row, content, event.transferMode)
        }
        if (parent.acceptDropOnNonRow() != null) {
            return parent.droppedFilesOnNonRow(content, event.transferMode)
        }
        return false
    }

    override fun accept(event: DragEvent): Array<TransferMode>? {
        val pair = parent.tool.findTableRow(event)
        val row = pair.first
        if (row != null) {
            parent.acceptDropOnRow(row)?.let { return it }
        }

        if (event.gestureSource === source) {
            return null
        }

        return parent.acceptDropOnNonRow()
    }
}
