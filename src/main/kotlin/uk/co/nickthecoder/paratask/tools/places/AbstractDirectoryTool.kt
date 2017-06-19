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

package uk.co.nickthecoder.paratask.tools.places

import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DragFiles
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.util.*
import java.io.File

abstract class AbstractDirectoryTool(name: String, description: String)

    : AbstractTableTool<WrappedFile>(), HasDirectory {


    final override val taskD = TaskDescription(name = name, description = description)

    val directoryP = FileParameter("directory", expectFile = false, mustExist = true)

    val depthP = IntParameter("depth", value = 1, range = 1..Int.MAX_VALUE)

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)

    val extensionsP = MultipleParameter("extensions") { StringParameter("") }

    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val enterHiddenP = BooleanParameter("enterHidden", value = false)

    val includeBaseP = BooleanParameter("includeBase", value = false)

    val thumbnailHeightP = IntParameter("thumbnailHeight", value = 32)

    val thumbnailer = Thumbnailer()

    override val directory: File? by directoryP


    init {
        taskD.addParameters(directoryP, depthP, onlyFilesP, extensionsP, includeHiddenP, enterHiddenP, includeBaseP, thumbnailHeightP)
    }

    override fun createColumns() {
        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { createImageView(it) })
        if (isTree()) {
            columns.add(BaseFileColumn<WrappedFile>("path", base = directoryP.value!!) { it.file })
        } else {
            columns.add(FileNameColumn<WrappedFile>("name") { it.file })
        }
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })
    }

    fun createImageView(row: WrappedFile): ImageView {
        var result: ImageView? = null

        if (row.file.isImage()) {
            val thumbnail = thumbnailer.thumbnailImage(row.file)
            if (thumbnail != null) {
                result = ImageView()
                result.image = thumbnail
                result.fitHeight = thumbnailHeightP.value!!.toDouble()
                result.isPreserveRatio = true
                result.isSmooth = true
            }
        }

        if (result == null) {
            result = ImageView(row.icon)
        }

        return result
    }

    fun droppedFiles(event: DragEvent): Boolean {

        val (row, _) = findTableRow(event)
        if (row != null && row.isDirectory()) {
            val dir = row.file
            return droppedFiles(dir, event.dragboard.files, event.transferMode)
        }

        if (!isTree()) {
            val dir = directory
            if (dir != null) {
                return droppedFiles(dir, event.dragboard.files, event.transferMode)
            }
        }

        return false
    }

    fun droppedFiles(dest: File, files: List<File>, transferMode: TransferMode): Boolean {

        if (transferMode == TransferMode.COPY) {
            FileOperations.instance.copyFiles(files, dest)
            return true
        }
        return false
    }

    override fun createHeaderRows(): List<HeaderRow> = listOf(HeaderRow().add(directoryP))


    override fun createTableResults(): TableResults<WrappedFile> {
        val tableResults = super.createTableResults()

        DragFiles(tableResults.tableView) {
            tableResults.tableView.selectionModel.selectedItems.map { it.row.file }
        }

        TableDropFiles(tableResults.tableView) { event ->
            droppedFiles(event)
        }

        return tableResults
    }

    override fun run() {
        shortTitle = directory?.name ?: "Directory"
        longTitle = "Directory ${directory?.path}"

        val lister = FileLister(
                depth = depthP.value!!,
                onlyFiles = onlyFilesP.value,
                includeHidden = includeHiddenP.value!!,
                enterHidden = enterHiddenP.value!!,
                includeBase = includeBaseP.value!!,
                extensions = extensionsP.value
        )

        list.clear()
        list.addAll(lister.listFiles(directoryP.value!!).map { WrappedFile(it) })
    }

    fun isTree(): Boolean = depthP.value!! > 1


    /**
     * Style a ROW when dragging files to a directory, otherwise, style the table as a whole
     */
    inner class TableDropFiles(target: Node,
                               source: Node = target,
                               modes: Array<TransferMode> = TransferMode.ANY,
                               dropped: (DragEvent) -> Boolean)
        : DropFiles(target, source, modes, dropped) {

        override fun styleableNode(event: DragEvent): Styleable? {
            val (row, tableRow) = findTableRow(event)
            if (row?.isDirectory() == true) {
                return tableRow
            }

            if (isTree()) {
                // Cannot copy to a directory tree, as there are multiple directories on display
                return null
            }

            return super.styleableNode(event)
        }
    }
}

