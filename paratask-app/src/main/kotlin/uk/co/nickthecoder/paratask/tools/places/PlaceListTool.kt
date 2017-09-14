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
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.table.TruncatedStringColumn
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File

class PlaceListTool : ListTableTool<Place>(), SingleRowFilter<Place> {

    override val taskD = TaskDescription("placesList", description = "Favourite Places")

    val placesP = MultipleParameter("files") {
        PlaceParameter()
    }

    override val rowFilter = RowFilter<Place>(this, columns, PlaceInFile(PlacesFile(File("")), Resource(File("")), ""))

    init {
        taskD.addParameters(placesP)

        columns.add(Column<Place, ImageView>("icon", label = "", getter = { ImageView(it.resource.icon) }))
        columns.add(Column<Place, String>("label", getter = { it.label }))
        columns.add(TruncatedStringColumn<Place>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS, getter = { it.name }))
        columns.add(Column<Place, String>("location", getter = { it.resource.path }, filterGetter = { it.resource }))
    }

    override fun run() {
        placesP.value.filterIsInstance<PlaceParameter>().forEach { placeP ->
            try {
                val label = placeP.labelP.value
                val resource = if (placeP.oneOfP.value == placeP.fileP) {
                    Resource(placeP.fileP.value!!)
                } else {
                    Resource(placeP.urlP.value)
                }
                list.add(Place(resource, label))
            } catch (e: Exception) {
                // Do nothing, just exclude the item from the list.
            }
        }
    }

    class PlaceParameter : CompoundParameter("places") {

        val labelP = StringParameter("label", required = false)

        val fileP = FileParameter("file", expectFile = null, required = false)

        val urlP = StringParameter("url", required = false)

        val oneOfP = OneOfParameter("fileOrURL", value = fileP)

        init {
            oneOfP.addParameters(fileP, urlP)

            addParameters(labelP, oneOfP)
        }
    }

}

fun main(args: Array<String>) {
    TaskParser(PlaceListTool()).go(args)
}
