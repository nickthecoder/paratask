package uk.co.nickthecoder.paratask.table

import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.misc.FileOperations
import java.io.File

/**
 * A helper class for table tools which can be a drop target for files.
 * When dropping onto the table, you can distiguish between dropping to the table as a whole,
 * or dropping to a single row of the table.
 * R is the type or Row
 */
abstract class TableDropFilesHelper<R : Any>(
        tool: TableTool<R>,
        modes: Array<TransferMode> = TransferMode.ANY)

    : TableDropHelper<List<File>, R>(
        dataFormat = DataFormat.FILES,
        tool = tool,
        modes = modes) {

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

}
