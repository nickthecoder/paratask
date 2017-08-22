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

import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.misc.ThreadedDesktop
import uk.co.nickthecoder.paratask.misc.Thumbnailer
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.project.ResultsWithHeader
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.isImage
import java.io.File

abstract class AbstractDirectoryTool(name: String, description: String)

    : AbstractTableTool<WrappedFile>(), HasDirectory {

    final override val taskD = TaskDescription(name = name, description = description)

    val directoriesP = MultipleParameter("directories", value = listOf(currentDirectory)) {
        FileParameter("dir", label = "Directory", expectFile = false, mustExist = true)
    }

    val depthP = IntParameter("depth", value = 1, range = 1..Int.MAX_VALUE, columnCount = 3)

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)

    val extensionsP = MultipleParameter("extensions") { StringParameter("") }

    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val enterHiddenP = BooleanParameter("enterHidden", value = false)

    val includeBaseP = BooleanParameter("includeBase", value = false)

    val thumbnailHeightP = IntParameter("thumbnailHeight", value = 32)

    val thumbnailer = Thumbnailer()

    override val directory: File?
        get() {
            return selectedDirectoryTableResults()?.directory
        }

    /**
     * The results Map of directory to list of files listed for the directory.
     */
    var lists = mutableMapOf<File, List<WrappedFile>>()


    init {
        taskD.addParameters(directoriesP, depthP, onlyFilesP, extensionsP, includeHiddenP, enterHiddenP, includeBaseP, thumbnailHeightP)
    }

    override fun loadProblem(parameterName: String, expression: String?, stringValue: String?) {
        if (parameterName == "directory") {
            directoriesP.clear()
            if (expression != null) {
                val inner = directoriesP.newValue()
                inner.expression = expression
            } else {
                directoriesP.addValue(File(stringValue))
            }
        } else {
            super.loadProblem(parameterName, expression, stringValue)
        }
    }

    fun createColumns(directory: File): List<Column<WrappedFile, *>> {
        val columns = mutableListOf<Column<WrappedFile, *>>()

        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { createImageView(it) })
        if (isTree()) {
            columns.add(BaseFileColumn<WrappedFile>("path", base = directory) { it.file })
        } else {
            columns.add(FileNameColumn<WrappedFile>("name") { it.file })
        }
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })

        return columns
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
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

    open fun createHeaderRows(dirP: FileParameter): Header = Header(this, dirP)

    fun createResults(dirP: FileParameter): Results {
        val dir = dirP.value!!
        val list = lists[dir]!!
        val tableResults = DirectoryTableResults(dir, list)

        return ResultsWithHeader(tableResults, createHeaderRows(dirP))
    }

    override fun createResults(): List<Results> {
        return directoriesP.innerParameters.filter { it.value != null }.map { dirP ->
            createResults(dirP as FileParameter)
        }
    }

    override fun run() {
        shortTitle = directory?.name ?: "Directory"
        longTitle = "Directory ${directory?.path}"

        directoriesP.value.filterNotNull().forEach { dir ->
            val lister = FileLister(
                    depth = depthP.value!!,
                    onlyFiles = onlyFilesP.value,
                    includeHidden = includeHiddenP.value!!,
                    enterHidden = enterHiddenP.value!!,
                    includeBase = includeBaseP.value!!,
                    extensions = extensionsP.value
            )

            lists[dir] = lister.listFiles(dir).map { WrappedFile(it) }
        }

    }

    open fun isTree(): Boolean = false

    fun selectedDirectoryTableResults(): DirectoryTableResults? {
        val res = toolPane?.currentResults()
        if (res is ResultsWithHeader) {
            val inner = res.results
            if (inner is DirectoryTableResults) {
                return inner
            }
        }
        return null
    }

    /**
     * Called from the default option - either opens the file, or changes the directory of just the current results tab.
     * This is needed, because this tool allow for multiple results, and we only want to change directory of ONE.
     */
    fun open(file: File) {
        if (file.isDirectory) {
            selectedDirectoryTableResults()?.let {
                directoriesP.replace(it.directory, file)
                toolPane?.parametersPane?.run()
            }
        } else {
            ThreadedDesktop.instance.open(file)
        }
    }

    inner class DirectoryDropHelper(val directory: File) : TableDropFilesHelper<WrappedFile>() {

        override fun acceptDropOnRow(row: WrappedFile) = if (row.isDirectory()) TransferMode.ANY else null

        override fun acceptDropOnNonRow() = if (isTree()) null else TransferMode.ANY

        override fun droppedOnRow(row: WrappedFile, content: List<File>, transferMode: TransferMode): Boolean {
            if (row.isDirectory()) {
                return fileOperation(row.file, content, transferMode)
            }
            return false
        }

        override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode): Boolean {
            return fileOperation(directory, content, transferMode)
        }
    }

    inner class DirectoryTableResults(val directory: File, list: List<WrappedFile>)
        : TableResults<WrappedFile>(this@AbstractDirectoryTool, list, directory.name, createColumns(directory), canClose = true) {

        init {
            dropHelper = DirectoryDropHelper(directory)

            dragHelper = DragFilesHelper {
                selectedRows().map { it.file }
            }
        }

        override fun closed() {
            directoriesP.remove(directory)
            // Hitting "Back" will un-close the tab ;-)
            toolPane?.halfTab?.pushHistory()
        }
    }
}
