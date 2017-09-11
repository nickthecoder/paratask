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
import uk.co.nickthecoder.paratask.gui.*
import uk.co.nickthecoder.paratask.misc.AutoRefresh
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class PlacesDirectoryTool : ListTableTool<Place>() {

    override val taskD = TaskDescription("placesDirectory", description = "Places Directory")

    val directoryP = FileParameter("directory", expectFile = false,
            value = homeDirectory.child(".config", "paratask", "places"))

    val fileLister = FileLister()

    val filenameP = directoryP.createFileChoicesParameter(fileLister)

    lateinit var placesFile: PlacesFile

    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.runIfNotAlreadyRunning() }

    override val rowFilter = RowFilter<Place>(this, columns, Place(PlacesFile(File("")), Resource(File("")), ""))


    init {
        taskD.addParameters(directoryP, filenameP)

        columns.add(Column<Place, ImageView>("icon", label = "", getter = { ImageView(it.resource.icon) }))
        columns.add(Column<Place, String>("label", getter = { it.label }))
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS) { it.name })
        columns.add(Column<Place, String>("location", getter = { it.resource.path }))
    }

    override fun createHeader() = Header(this, directoryP, filenameP)

    override fun createTableResults(): TableResults<Place> {
        val tableResults = super.createTableResults()

        val filesDragHelper = DragFilesHelper {
            tableResults.selectedRows().filter { it.isFile() }.map { it.file!! }
        }

        val placesDragHelper = SimpleDragHelper<List<Place>>(Place.dataFormat, onMoved = { list ->
            list.forEach {
                placesFile.remove(it)
            }
            placesFile.save()
        }) {
            tableResults.selectedRows()
        }

        tableResults.dragHelper = CompoundDragHelper(placesDragHelper, filesDragHelper)


        val filesDropHelper: TableDropFilesHelper<Place> = object : TableDropFilesHelper<Place>() {

            override fun acceptDropOnNonRow() = arrayOf(TransferMode.LINK)

            override fun acceptDropOnRow(row: Place) = if (row.isDirectory()) TransferMode.ANY else null

            override fun droppedOnRow(row: Place, content: List<File>, transferMode: TransferMode) {
                if (row.isDirectory()) {
                    FileOperations.instance.fileOperation(content, row.file!!, transferMode)
                }
            }

            override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {
                for (file in content) {
                    placesFile.places.add(Place(placesFile, Resource(file), file.name))
                }
                placesFile.save()
            }

        }

        val placesDropHelper = SimpleDropHelper<List<Place>>(Place.dataFormat, arrayOf(TransferMode.COPY, TransferMode.MOVE)) { _, content ->

            content.forEach {
                placesFile.places.add(Place(placesFile, it.resource, it.label))
            }
            placesFile.save()
        }

        tableResults.dropHelper = CompoundDropHelper(placesDropHelper, filesDropHelper)


        return tableResults
    }

    override fun run() {
        val file = File(directoryP.value!!, filenameP.value!!)
        placesFile = PlacesFile(file)
        list = placesFile.places
        autoRefresh.unwatchAll()
        autoRefresh.watch(file)
    }

    override fun detaching() {
        autoRefresh.unwatchAll()
        super<ListTableTool>.detaching()
    }

    fun taskNew() = placesFile.taskNew()

    fun taskNewPlacesFile() = NewPlacesFileTask(directory = directoryP.value!!)
}


fun main(args: Array<String>) {
    TaskParser(PlacesDirectoryTool()).go(args)
}
