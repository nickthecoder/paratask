package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.PlacesFile
import uk.co.nickthecoder.paratask.util.PlacesFile.Place
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory

class PlacesTool : AbstractTool() {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val file = FileParameter("file", value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    private lateinit var placesFile: PlacesFile

    override fun run() {
        placesFile = PlacesFile(file.value!!)
    }

    override fun createResults(): List<Results> = singleResults(PlacesResults())

    fun taskNew() = placesFile.taskNew()

    inner class PlacesResults() : AbstractTableResults<Place>(this@PlacesTool, placesFile.places) {

        init {
            columns.add(Column<Place, ImageView>("icon", label = "") { ImageView(it.icon) })
            columns.add(Column<Place, String>("label") { it.label })
            columns.add(Column<Place, String>("url") { it.urlString })
        }
    }
}


fun main(args: Array<String>) {
    CommandLineTool(PlacesTool()).go(args)
}
