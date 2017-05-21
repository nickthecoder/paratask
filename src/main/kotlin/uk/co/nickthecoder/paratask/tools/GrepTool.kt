package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.FileNameColumn
import uk.co.nickthecoder.paratask.table.NumberColumn
import uk.co.nickthecoder.paratask.tools.GrepTool.GrepRow
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

/**
 * Converts the text output from GrepTask's into a List, so that the results can be displayed as a table.
 */
class GrepTool : AbstractCommandTool<GrepRow>(), Stoppable {

    val grepTask = GrepTask()

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

    override fun createHeaderRows(): List<HeaderRow> {
        val row1 = HeaderRow()
        row1.add(grepTask.filesP)
        val row2 = HeaderRow()
        row2.add(grepTask.patternsP)
        row2.add(grepTask.typeP)
        row2.add(grepTask.matchCaseP)
        return listOf(row1, row2)
    }


    override fun createCommand(): OSCommand = grepTask.run()

    override fun processLine(line: String) {
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

    override fun check() {
        grepTask.check()
    }

    data class GrepRow(val file: File, var lineNumber: Int, var line: String)

}

fun main(args: Array<String>) {
    ToolParser(GrepTool()).go(args)
}
