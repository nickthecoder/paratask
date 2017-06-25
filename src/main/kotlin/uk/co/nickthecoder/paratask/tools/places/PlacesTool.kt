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
import uk.co.nickthecoder.paratask.gui.ToolDropFiles
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.table.TruncatedStringColumn
import uk.co.nickthecoder.paratask.util.*
import java.io.File

class PlacesTool : AbstractTableTool<Place>(), AutoRefreshTool {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val fileP = FileParameter("file", value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    lateinit var placesFile: PlacesFile

    var dropFiles: ToolDropFiles<Place> = object : ToolDropFiles<Place>(this, modes = arrayOf(TransferMode.LINK), rowModes = TransferMode.ANY) {

        override fun acceptDropOnRow(row: Place) = row.isDirectory()

        override fun droppedFilesOnRow(row: Place, files: List<File>, transferMode: TransferMode): Boolean {
            if (row.isFile()) {
                return fileOperation(row.file!!, files, transferMode)
            }
            return false
        }

        override fun droppedFilesOnNonRow(files: List<File>, transferMode: TransferMode): Boolean {
            for (file in files) {
                placesFile.places.add(Place(placesFile, Resource(file), file.name))
            }
            placesFile.save()
            return true
        }

    }

    init {
        taskD.addParameters(fileP)
    }

    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.resource.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS) { it.name })
        columns.add(Column<Place, String>("location") { it.resource.path })
    }

    override fun createTableResults(): TableResults<Place> {
        val tableResults = super.createTableResults()

        dropFiles.table = tableResults.tableView
        return tableResults
    }

    override fun run() {
        placesFile = PlacesFile(fileP.value!!)
        list = placesFile.places
        watch(fileP.value!!)
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        dropFiles.attached(toolPane)
    }


    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractTableTool>.detaching()
        dropFiles.detaching()
    }

    fun taskNew() = placesFile.taskNew()
}


fun main(args: Array<String>) {
    TaskParser(PlacesTool()).go(args)
}
