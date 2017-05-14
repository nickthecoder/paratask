package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.PlacesFile
import uk.co.nickthecoder.paratask.util.PlacesFile.Place
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory

class PlacesTool : AbstractTableTool<Place>() {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val file = FileParameter("file", value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    private lateinit var placesFile: PlacesFile

    override fun createColumns() {
        columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(Column<Place, String>("label") { it.label })
        columns.add(Column<Place, String>("url") { it.urlString })
    }

    override fun run() {
        placesFile = PlacesFile(file.value!!)
    }

    fun taskNew() = placesFile.taskNew()

}


fun main(args: Array<String>) {
    CommandLineTool(PlacesTool()).go(args)
}
