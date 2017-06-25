package uk.co.nickthecoder.paratask.gui

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.control.TableView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.WrappedRow
import uk.co.nickthecoder.paratask.util.FileOperations
import java.io.File

/**
 * A helper class for tools which can be a drop target for files.
 * The files can either be dropped onto the tab, or onto the table
 * R is the type or Row
 */
abstract class ToolDropFiles<R : Any>(
        val tool: AbstractTableTool<R>,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val rowModes: Array<TransferMode> = modes
) {

    var dropFilesOnTab: DropFiles? = null

    var dropFilesOnTable: DropFilesOnTable? = null

    var table: TableView<WrappedRow<R>>? = null
        set(v) {
            v?.let {
                dropFilesOnTable = DropFilesOnTable(v, modes = rowModes) { event ->
                    droppedFilesOnTable(event)
                }
            }
        }

    fun attached(toolPane: ToolPane) {
        val halfTab = toolPane.halfTab
        if (halfTab.isLeft()) {
            dropFilesOnTab = DropFiles(halfTab.projectTab as Node, modes = modes) { event ->
                droppedFilesOnNonRow(event.dragboard.files, event.transferMode)
            }
        }
    }

    fun detaching() {
        dropFilesOnTab?.let {
            it.cancel()
            dropFilesOnTab = null
        }
    }

    fun droppedFilesOnTable(event: DragEvent): Boolean {

        val (row, _) = tool.findTableRow(event)
        if (row != null && acceptDropOnRow(row)) {
            return droppedFilesOnRow(row, event.dragboard.files, event.transferMode)
        }
        if (acceptDropOnNonRow()) {
            return droppedFilesOnNonRow(event.dragboard.files, event.transferMode)
        }
        return false
    }

    abstract fun acceptDropOnRow(row: R): Boolean

    abstract fun droppedFilesOnRow(row: R, files: List<File>, transferMode: TransferMode): Boolean

    abstract fun droppedFilesOnNonRow(files: List<File>, transferMode: TransferMode): Boolean

    open fun acceptDropOnNonRow() = true

    fun fileOperation(dest: File, files: List<File>, transferMode: TransferMode): Boolean {

        when (transferMode) {
            TransferMode.COPY -> {
                FileOperations.instance.copyFiles(files, dest)
                return true
            }
            TransferMode.MOVE -> {
                FileOperations.instance.moveFiles(files, dest)
            }
            TransferMode.LINK -> {
                FileOperations.instance.linkFiles(files, dest)
            }
        }
        return false
    }

    /**
     * Style a ROW when dragging files to a directory, otherwise, style the table as a whole
     */
    inner class DropFilesOnTable(target: Node,
                                 source: Node = target,
                                 modes: Array<TransferMode> = TransferMode.ANY,
                                 dropped: (DragEvent) -> Boolean)
        : DropFiles(target, source, modes, dropped) {

        override fun styleableNode(event: DragEvent): Styleable? {
            val (row, tableRow) = tool.findTableRow(event)
            if (row != null && acceptDropOnRow(row)) {
                return tableRow
            }

            if (acceptDropOnNonRow()) {
                return super.styleableNode(event)
            }

            return null
        }

        override fun accept(event: DragEvent): Boolean {
            if (event.gestureSource === source) {
                val pair = tool.findTableRow(event)
                val row = pair.first
                if (row != null) {
                    return acceptDropOnRow(row)
                }
            }
            return super.accept(event)
        }
    }
}