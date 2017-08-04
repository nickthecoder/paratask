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

import javafx.scene.control.OverrunStyle
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.table.TableToolDropFilesHelper
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.table.TruncatedStringColumn
import uk.co.nickthecoder.paratask.util.*
import java.io.File

class PlacesDirectoryTool : AbstractTableTool<Place>(), AutoRefreshTool {

    override val taskD = TaskDescription("placesDirectory", description = "Places Directory")

    val directoryP = FileParameter("directory", expectFile = false,
            value = homeDirectory.child(".config", "paratask", "places"))

    val fileLister = FileLister()

    val filenameP = directoryP.createFileChoicesParameter(fileLister)

    lateinit var placesFile: PlacesFile


    var dropHelper: TableToolDropFilesHelper<Place> = object : TableToolDropFilesHelper<Place>(this, modes = TransferMode.ANY) {

        override fun acceptDropOnNonRow() = arrayOf(TransferMode.LINK)

        override fun acceptDropOnRow(row: Place) = if (row.isDirectory()) TransferMode.ANY else null

        override fun droppedFilesOnRow(row: Place, content: List<File>, transferMode: TransferMode): Boolean {
            if (row.isDirectory()) {
                return fileOperation(row.file!!, content, transferMode)
            }
            return false
        }

        override fun droppedFilesOnNonRow(content: List<File>, transferMode: TransferMode): Boolean {
            for (file in content) {
                placesFile.places.add(Place(placesFile, Resource(file), file.name))
            }
            placesFile.save()
            return true
        }

    }

    init {
        taskD.addParameters(directoryP, filenameP)
    }

    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.resource.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS) { it.name })
        columns.add(Column<Place, String>("location") { it.resource.path })
    }

    override fun createHeaderRows(): List<HeaderRow> {
        return listOf(HeaderRow().addAll(directoryP, filenameP))
    }

    override fun createTableResults(): TableResults<Place> {
        val tableResults = super.createTableResults()

        DragFilesHelper(tableResults.tableView) {
            tableResults.tableView.selectionModel.selectedItems.filter { it.row.isFileOrDirectory() }.map { it.row.file!! }
        }

        dropHelper.table = tableResults.tableView
        return tableResults
    }

    override fun run() {
        val file = File(directoryP.value!!, filenameP.value!!)
        placesFile = PlacesFile(file)
        list = placesFile.places
        watch(file)
    }


    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        dropHelper.attached(toolPane)
    }

    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractTableTool>.detaching()
        dropHelper.detaching()
    }

    fun taskNew() = placesFile.taskNew()

    fun taskNewPlacesFile() = NewPlacesFileTask(directory = directoryP.value!!)
}


fun main(args: Array<String>) {
    TaskParser(PlacesDirectoryTool()).go(args)
}
