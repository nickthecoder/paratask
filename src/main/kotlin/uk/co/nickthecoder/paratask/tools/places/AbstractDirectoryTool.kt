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

import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DragFiles
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
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
        if (row.file.isImage()) {
            val thumbnail = thumbnailer.thumbnailImage(row.file)
            thumbnail?.let {
                val iv = ImageView()
                iv.image = thumbnail
                iv.fitHeight = thumbnailHeightP.value!!.toDouble()
                iv.isPreserveRatio = true
                iv.isSmooth = true
                return iv
            }
        }
        return ImageView(row.icon)
    }

    override fun createHeaderRows(): List<HeaderRow> = listOf(HeaderRow().add(directoryP))


    override fun createTableResults(): TableResults<WrappedFile> {
        val tableResults = super.createTableResults()

        DragFiles(tableResults.tableView) {
            tableResults.tableView.selectionModel.selectedItems.map { it.row.file }
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

    abstract fun isTree(): Boolean

}
