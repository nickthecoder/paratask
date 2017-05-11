package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.HasFile
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File
import java.net.URL

class PlacesTool : AbstractTool() {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val file = FileParameter("file", value = homeDirectory.child(".config", "gtk-3.0", "bookmarks"))

    private var results: List<Place> = listOf<Place>()

    override fun run() {
    }

    override fun updateResults() {

        results = file.value!!.readLines().map { Place(it) }

        toolPane?.updateResults(PlacesResults())
    }

    class Place(
            val line: String
    ) : HasFile, Labelled {

        val url: URL

        override val label: String

        init {
            val space = line.indexOf(' ')
            val urlString = if (space >= 0) line.substring(0, space) else line
            url = URL(urlString)
            val filePart = File(url.getFile())
            label = if (space >= 0) line.substring(space).trim() else filePart.name
        }

        override val file: File by lazy { File(url.toURI()) }
    }

    inner class PlacesResults() : AbstractTableResults<Place>(this@PlacesTool, results) {

        init {
            //columns.add(Column<Tool, ImageView>("icon", label = "") { tool -> ImageView(tool.icon) })
            columns.add(Column<Place, String>("label") { it.label })
            columns.add(Column<Place, URL>("url") { it.url })
        }
    }
}


fun main(args: Array<String>) {
    CommandLineTool(PlacesTool()).go(args)
}
