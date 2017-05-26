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
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.AutoRefreshTool
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class PlacesDirectoryTool : AbstractTableTool<Place>(), AutoRefreshTool {

    override val taskD = TaskDescription("placesDirectory", description = "Places Directory")

    val directoryP = FileParameter("directory", expectFile = false,
            value = homeDirectory.child(".config", "paratask", "places"))

    val fileLister = FileLister()

    val filenameP = directoryP.createFileChoicesParameter(fileLister)

    private lateinit var placesFile: PlacesFile

    init {
        taskD.addParameters(directoryP, filenameP)
    }

    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(Column<Place, String>("name") { it.name })
        columns.add(Column<Place, String>("url") { it.urlString })
    }

    override fun createHeaderRows(): List<HeaderRow> {
        return listOf(HeaderRow().addAll(directoryP, filenameP))
    }

    override fun run() {
        val file = File(directoryP.value!!, filenameP.value!!)
        placesFile = PlacesFile(file)
        list = placesFile.places
        watch(file)
    }

    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractTableTool>.detaching()
    }

    fun taskNew() = placesFile.taskNew()

}


fun main(args: Array<String>) {
    TaskParser(PlacesDirectoryTool()).go(args)
}
