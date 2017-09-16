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
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.compound.ResourceParameter
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File

class PlaceListTool : ListTableTool<Place>(), SingleRowFilter<Place> {

    override val taskD = TaskDescription("placesList", description = "Favourite Places")

    val placesP = MultipleParameter("files") {
        PlaceParameter()
    }

    override val rowFilter = RowFilter(this, columns, PlaceInFile(PlacesFile(File("")), Resource(File("")), ""))

    init {
        taskD.addParameters(placesP)

        columns.add(Column<Place, ImageView>("icon", label = "", getter = { ImageView(it.resource.icon) }))
        columns.add(Column<Place, String>("label", getter = { it.label }))
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS, getter = { it.name }))
        columns.add(Column<Place, String>("location", getter = { it.resource.path }, filterGetter = { it.resource }))
    }

    override fun run() {
        list.clear()
        list.addAll(placesP.value.filterIsInstance<PlaceParameter>().map { placeP -> placeP.createPlace() }.filterNotNull())
    }

    override fun createTableResults(): TableResults<Place> {
        val tableResults = super.createTableResults()

        // Drag

        val filesDragHelper = DragFilesHelper {
            tableResults.selectedRows().filter { it.isFile() }.map { it.file!! }
        }

        val placesDragHelper = SimpleDragHelper(Place.dataFormat, onMoved = { list ->
            list.forEach { place ->
                placesP.value.filterIsInstance<PlaceParameter>().firstOrNull { it.createPlace() == place }?.let {
                    placesP.remove(it)
                }
            }
            toolPane?.parametersPane?.run()
        }) {
            tableResults.selectedRows()
        }

        tableResults.dragHelper = CompoundDragHelper(placesDragHelper, filesDragHelper)

        // Drop

        val filesDropHelper: TableDropFilesHelper<Place> = object : TableDropFilesHelper<Place>() {

            override fun acceptDropOnNonRow() = arrayOf(TransferMode.LINK)

            override fun acceptDropOnRow(row: Place) = if (row.isDirectory()) TransferMode.ANY else null

            override fun droppedOnRow(row: Place, content: List<File>, transferMode: TransferMode) {
                if (row.isDirectory()) {
                    FileOperations.instance.fileOperation(content, row.file!!, transferMode)
                }
            }

            override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {
                for (f in content) {
                    val newEntry = placesP.newValue() as PlaceParameter
                    newEntry.labelP.value = f.name
                    newEntry.resource = Resource(f)
                }
            }
        }

        val placesDropHelper = SimpleDropHelper<List<Place>>(Place.dataFormat, arrayOf(TransferMode.COPY, TransferMode.MOVE)) { _, content ->

            content.forEach { place ->
                val newEntry = placesP.newValue() as PlaceParameter
                newEntry.labelP.value = place.label
                newEntry.resource = place.resource
            }
            toolPane?.parametersPane?.run()

        }

        tableResults.dropHelper = CompoundDropHelper(placesDropHelper, filesDropHelper)

        return tableResults
    }

    class PlaceParameter : MultipleGroupParameter("places") {

        val labelP = StringParameter("label", required = false)

        val resourceP = ResourceParameter("resource")
        var resource by resourceP

        init {
            addParameters(labelP, resourceP)
        }

        fun createPlace(): Place? {
            resource?.let {
                return Place(it, labelP.value)
            }
            return null
        }

    }

}

fun main(args: Array<String>) {
    TaskParser(PlaceListTool()).go(args)
}
