package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import java.io.File

/**
 * A helper class for table tools which can be a drop target for files.
 * The files can either be dropped onto the tab, or onto the table (i.e. this class has TWO DropHelper instances).
 * When dropping onto the table, you can distiguish between dropping to the table as a whole,
 * or dropping to a single row of the table.
 * R is the type or Row
 */
abstract class TableToolDropFilesHelper<R : Any>(
        tool: AbstractTableTool<R>,
        modes: Array<TransferMode> = TransferMode.ANY)
    : TableToolDropHelper<List<File>?, R>(dataFormat = DataFormat.FILES, tool = tool, modes = modes) {
}
