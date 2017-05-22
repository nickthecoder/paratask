package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class PlacesDirectoryTool : AbstractTableTool<Place>() {

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
        return listOf(HeaderRow().add(filenameP))
    }

    override fun run() {
        placesFile = PlacesFile(File(directoryP.value!!, filenameP.value!!))
        list = placesFile.places
    }

    fun taskNew() = placesFile.taskNew()

}


fun main(args: Array<String>) {
    ToolParser(PlacesDirectoryTool()).go(args)
}
