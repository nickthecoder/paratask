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

package uk.co.nickthecoder.paratask.tools.git

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.misc.FileTest
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.table.WrappedRow
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class GitStatusTool :
        AbstractCommandTool<GitStatusTool.GitStatusRow>(),
        HasDirectory,
        SingleRowFilter<GitStatusTool.GitStatusRow> {

    override val taskD = TaskDescription("gitStatus", description = "Source Code Control")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory: File? by directoryP

    override val resultsName = "Status"

    private val exampleRow = GitStatusRow(File(""), ' ', ' ')

    init {
        taskD.addParameters(directoryP)

        columns.add(Column<GitStatusRow, Char>("index", getter = { it.index }))
        columns.add(Column<GitStatusRow, Char>("work", getter = { it.work }))
        columns.add(Column<GitStatusRow, String>("name", getter = { it.file.name }))
        columns.add(BaseFileColumn<GitStatusRow>("path", base = directory!!, getter = { it.file }))
        columns.add(Column<GitStatusRow, String?>("renamedFrom", getter = { it.renamed }))
    }

    override val rowFilter = RowFilter(this, columns, exampleRow, "Git Status Filter")

    override fun createHeader() = Header(this, directoryP)

    override fun createCommand(): OSCommand {
        longTitle = "Git $directory"
        return OSCommand("git", "status", "--porcelain").dir(directory!!)
    }

    override fun processLine(line: String) {

        if (line.length < 4) {
            return
        }

        val index = line[0]
        val work = line[1]
        var path = line.substring(3)
        var renamed: String? = null
        val arrow = path.indexOf(" -> ")
        if (arrow >= 0) {
            renamed = path.substring(0, arrow)
            path = path.substring(arrow + 4)
        }
        val file = File(directory, path)

        val gsr = GitStatusRow(file, index = index, work = work, renamed = renamed)
        if (rowFilter.accept(gsr)) {
            list.add(gsr)
        }
    }

    fun addDirectory(directory: File, index: Char, work: Char) {
        val fileLister = FileLister(depth = 10, includeHidden = true)
        val listing = fileLister.listFiles(directory)
        for (file in listing) {
            list.add(GitStatusRow(file, index = index, work = work))
        }
    }

    override fun updateRow(tableRow: TableRow<WrappedRow<GitStatusRow>>, row: GitStatusRow) {
        val style = if (row.index == '?') {
            "untracked"
        } else if (row.work == 'M') {
            "not-updated"
        } else if (row.index == 'R') {
            "renamed"
        } else if (row.index == 'M') {
            "updated"
        } else {
            "normal"
        }
        tableRow.styleClass.add("git-" + style)
    }

    override fun createTableResults(): TableResults<GitStatusRow> {
        val results = super.createTableResults()

        results.dragHelper = DragFilesHelper {
            results.selectedRows().map { it.file }
        }

        return results
    }

    inner class GitStatusRow(
            override val file: File,
            val index: Char,
            val work: Char,
            val renamed: String? = null) : FileTest {

        val path: String

        init {
            val filePath = file.path
            val prefix = directory!!.path + File.separatorChar
            if (filePath.startsWith(prefix)) {
                path = filePath.substring(prefix.length)
            } else {
                path = filePath
            }
        }
    }

}

fun main(args: Array<String>) {
    TaskParser(GitStatusTool()).go(args)
}
