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
import javafx.scene.control.TableRow
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.CompoundDragHelper
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.gui.DragHelper
import uk.co.nickthecoder.paratask.misc.AutoRefreshTool
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class PlacesTool : AbstractTableTool<Place>(), AutoRefreshTool {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val fileP = FileParameter("file", mustExist = null, value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    lateinit var placesFile: PlacesFile

    var filesDropHelper: TableToolDropFilesHelper<Place> = object : TableToolDropFilesHelper<Place>(this) {

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

    var placesDropHelper: TableToolDropHelper<List<Place>, Place> =
            object : TableToolDropHelper<List<Place>, Place>(Place.dataFormat, this, allowLink = false) {

                override fun droppedFilesOnNonRow(content: List<Place>, transferMode: TransferMode): Boolean {
                    content.forEach {
                        placesFile.places.add(Place(placesFile, it.resource, it.label))
                    }
                    placesFile.save()
                    return true
                }
            }

    val compoundDropHelper = CompoundToolDropHelper<Place>(this, placesDropHelper, filesDropHelper)

    init {
        taskD.addParameters(fileP)
    }

    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.resource.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS) { it.name })
        columns.add(Column<Place, String>("location") { it.resource.path })
    }

    var filesDragHelper = DragFilesHelper {
        selectedRows().filter { it.isFile() }.map { it.file!! }
    }
    var placesDragHelper = DragHelper<List<Place>>(Place.dataFormat, onMoved = { list ->
        println("Moved $list")
        list.forEach {
            placesFile.remove(it)
        }
        placesFile.save()
    }) {
        selectedRows()
    }
    var compoundDragHelper = CompoundDragHelper(placesDragHelper, filesDragHelper)

    override fun createTableResults(): TableResults<Place> {
        val tableResults = super.createTableResults()

        compoundDropHelper.attachTableResults(tableResults)
        return tableResults
    }

    override fun createRow(): TableRow<WrappedRow<Place>> {
        val row = super.createRow()
        compoundDragHelper.applyTo(row)
        return row
    }

    override fun run() {
        placesFile = PlacesFile(fileP.value!!)
        list = placesFile.places
        watch(fileP.value!!)
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        compoundDropHelper.attachToolPane(toolPane)
    }


    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractTableTool>.detaching()
        compoundDropHelper.detaching()
    }

    fun taskNew() = placesFile.taskNew()
}


fun main(args: Array<String>) {
    TaskParser(PlacesTool()).go(args)
}
