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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.FileNameColumn
import uk.co.nickthecoder.paratask.table.NumberColumn
import uk.co.nickthecoder.paratask.tools.GrepTool.GrepRow
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

/**
 * Converts the text output from GrepTask's into a List, so that the results can be displayed as a table.
 */
class GrepTool : AbstractCommandTool<GrepRow>(), Stoppable, HasDirectory {

    val grepTask = GrepTask()

    override val taskD = grepTask.taskD

    override val directory: File?
        get() = grepTask.filesP.value.firstOrNull { it?.isDirectory() == true }


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
    TaskParser(GrepTool()).go(args)
}
