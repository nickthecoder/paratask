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
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory

class PlacesTool : AbstractTableTool<Place>() {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val fileP = FileParameter("file", value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    private lateinit var placesFile: PlacesFile

    init {
        taskD.addParameters( fileP )
    }
    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(Column<Place, String>("name") { it.name })
        columns.add(Column<Place, String>("url") { it.urlString })
    }

    override fun run() {
        placesFile = PlacesFile(fileP.value!!)
        list = placesFile.places
    }

    fun taskNew() = placesFile.taskNew()

}


fun main(args: Array<String>) {
    ToolParser(PlacesTool()).go(args)
}