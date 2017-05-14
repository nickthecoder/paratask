package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.table.FileNameColumn
import uk.co.nickthecoder.paratask.project.table.NumberColumn
import uk.co.nickthecoder.paratask.project.task.GrepTool.GrepRow
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.HasFile
import java.io.File

/**
 * Converts the text output from GrepTask's into a List, so that the results can be displayed as a table.
 */
class GrepTool : AbstractTableTool<GrepRow>(), Stoppable {

    val grepTask = GrepTask()

    private var exec: Exec? = null

    override val taskD = grepTask.taskD

    init {
        grepTask.contextLinesP.hidden = true
        grepTask.additionalOptionsP.hidden = true
    }

    override fun createColumns() {
        columns.add(FileNameColumn<GrepRow>("name") { it.file })
        columns.add(NumberColumn<GrepRow, Int>("lineNumber", label = "#") { it.lineNumber })
        columns.add(Column<GrepRow, String>("line") { it.line })

    }

    override fun run() {

        val command = grepTask.run()
        list.clear()

        exec = Exec(command)
        exec?.outSink = BufferedSink() { line ->

            val colon1 = line.indexOf(':')
            val colon2 = line.indexOf(':', colon1 + 1)
            try {
                if (colon2 > 0) {
                    val file = File(line.substring(0, colon1))
                    val lineNumber = Integer.parseInt(line.substring(colon1 + 1, colon2))
                    val matchedLine = line.substring(colon2 + 1)
                    list.add(GrepRow(file, lineNumber, matchedLine))
                }
            } catch (e: Exception) {
                // Ignore errors from binary matches.
                // The version of grep I'm using seems to ignore the -I option when recursing. Grr.
            }
        }
        exec?.start()
        exec?.waitFor()
    }

    override fun stop() {
        exec?.kill()
    }


    override fun check() {
        grepTask.check()
    }

    data class GrepRow(override val file: File, var lineNumber: Int, var line: String) : HasFile {

    }

}

fun main(args: Array<String>) {
    CommandLineTool(GrepTool()).go(args)
}
