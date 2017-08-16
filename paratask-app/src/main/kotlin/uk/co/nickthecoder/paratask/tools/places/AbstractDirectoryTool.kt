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

import javafx.scene.control.TableRow
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.table.TableToolDropFilesHelper
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.HeaderRow
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.misc.*
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.isImage
import java.io.File

abstract class AbstractDirectoryTool(name: String, description: String)

    : AbstractTableTool<WrappedFile>(), HasDirectory {


    final override val taskD = TaskDescription(name = name, description = description)

    val directoryP = FileParameter("directory", expectFile = false, mustExist = true)

    val depthP = IntParameter("depth", value = 1, range = 1..Int.MAX_VALUE, columnCount = 3)

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)

    val extensionsP = MultipleParameter("extensions") { StringParameter("") }

    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val enterHiddenP = BooleanParameter("enterHidden", value = false)

    val includeBaseP = BooleanParameter("includeBase", value = false)

    val thumbnailHeightP = IntParameter("thumbnailHeight", value = 32)

    val thumbnailer = Thumbnailer()

    override val directory: File? by directoryP

    var dropHelper: TableToolDropFilesHelper<WrappedFile> = object : TableToolDropFilesHelper<WrappedFile>(this) {

        override fun acceptDropOnRow(row: WrappedFile) = if (row.isDirectory()) TransferMode.ANY else null

        override fun acceptDropOnNonRow() = if (isTree()) null else TransferMode.ANY

        override fun droppedFilesOnRow(row: WrappedFile, content: List<File>, transferMode: TransferMode): Boolean {
            if (row.isDirectory()) {
                return fileOperation(row.file, content, transferMode)
            }
            return false
        }

        override fun droppedFilesOnNonRow(content: List<File>, transferMode: TransferMode): Boolean {
            val dir = directory
            if (dir != null) {
                return fileOperation(dir, content, transferMode)
            }
            return false
        }
    }

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

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        dropHelper.attachToolPane(toolPane)
    }

    override fun detaching() {
        super.detaching()
        dropHelper.detaching()
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

    override fun createHeaderRows(): List<HeaderRow> = listOf(HeaderRow().add(directoryP))

    val dragHelper = DragFilesHelper {
        selectedRows().map { it.file }
    }

    override fun createTableResults(): TableResults<WrappedFile> {
        val tableResults = super.createTableResults()

        dropHelper.attachTableResults(tableResults)

        return tableResults
    }

    override fun createRow(): TableRow<WrappedRow<WrappedFile>> {
        val row = super.createRow()
        dragHelper.applyTo(row)
        return row
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

    open fun isTree(): Boolean = false
}

