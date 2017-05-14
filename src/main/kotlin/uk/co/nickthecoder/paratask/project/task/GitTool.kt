package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.BaseFileColumn
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.table.TableResults
import uk.co.nickthecoder.paratask.project.task.GitTool.GitStatusRow
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasFile
import java.io.File

// TODO Allow results to be filtered based on index and work?
// e.g. show changes, deletions, un

class GitTool : AbstractTableTool<GitStatusRow>() {

    override val taskD = TaskDescription("git", description = "Source Code Control")

    val directory = FileParameter("directory", expectFile = false)

    init {
        taskD.addParameters(directory)
    }

    override fun createColumns() {
        columns.add(Column<GitStatusRow, Char>("index") { it.index })
        columns.add(Column<GitStatusRow, Char>("work") { it.work })
        columns.add(Column<GitStatusRow, String>("name") { it.file.name })
        columns.add(BaseFileColumn<GitStatusRow>("path", base = directory.value!!) { it.file })
        columns.add(Column<GitStatusRow, String?>("renamedFrom") { it.renamed })
    }

    override fun run() {

        list.clear()

        val command = Command("git", "status", "--porcelain").dir(directory.value!!)
        val exec = Exec(command)
        exec.outSink = BufferedSink { processLine(it) }
        exec.start().waitFor()
    }

    fun processLine(line: String) {

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
        val file = File(directory.value, path)

        val gsr = GitStatusRow(file, index = index, work = work, renamed = renamed)
        list.add(gsr)
    }

    fun addDirectory(directory: File, index: Char, work: Char) {
        val fileLister = FileLister(depth = 10, includeHidden = true)
        val listing = fileLister.listFiles(directory)
        for (file in listing) {
            list.add(GitStatusRow(file, index = index, work = work))
        }
    }

    override fun createResults(): List<Results> {
        columns.clear()
        createColumns()
        return singleResults(GitStatusResults(this, list))
    }


    inner class GitStatusRow(
            override val file: File,
            val index: Char,
            val work: Char,
            val renamed: String? = null)
        : HasFile {

        val path: String

        init {
            val filePath = file.path
            val prefix = directory.value!!.path + File.separatorChar
            if (filePath.startsWith(prefix)) {
                path = filePath.substring(prefix.length)
            } else {
                path = filePath
            }
        }
    }


    class GitStatusResults(tool: GitTool, list: List<GitStatusRow>) : TableResults<GitStatusRow>(tool, list) {

        override fun updateRow(tableRow: CustomTableRow, row: GitStatusRow) {
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
            tableRow.getStyleClass().add("git-" + style)
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTool(GitTool()).go(args)
}