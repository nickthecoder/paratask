package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.table.AbstractTableResults
import uk.co.nickthecoder.paratask.project.table.BaseFileColumn
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasFile
import uk.co.nickthecoder.paratask.util.ListSink
import java.io.File

// TODO Allow results to be filtered based on index and work?
// e.g. show changes, deletions, un

class GitTool : AbstractTool() {

    override val taskD = TaskDescription("git", description = "Source Code Control")

    val directory = FileParameter("directory", value = File("/home/nick/tmp/test"))

    private var list = mutableListOf<GitStatusLine>()

    init {
        taskD.addParameters(directory)
    }

    override fun run() {

        list.clear()

        val command = Command("git", "status", "--porcelain").dir(directory.value!!)
        val exec = Exec(command)
        val listSink = ListSink()
        exec.outSink = listSink
        exec.start().waitFor()

        for (line in listSink.list) {
            if (line.length < 4) {
                continue;
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

            val gsl = GitStatusLine(file, index = index, work = work, renamed = renamed)
            list.add(gsl)
        }

    }

    fun addDirectory(directory: File, index: Char, work: Char) {
        val fileLister = FileLister().depth(10).includeHidden()
        val listing = fileLister.listFiles(directory)
        for (file in listing) {
            list.add(GitStatusLine(file, index = index, work = work))
        }
    }

    override fun updateResults() {
        toolPane?.updateResults(GitStatusResults(this, list))
    }


    inner class GitStatusLine(
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


    class GitStatusResults(tool: GitTool, list: List<GitStatusLine>) : AbstractTableResults<GitStatusLine>(tool, list) {

        init {
            columns.add(Column<GitStatusLine, Char>("index") { it.index })
            columns.add(Column<GitStatusLine, Char>("work") { it.work })
            columns.add(Column<GitStatusLine, String>("name") { it.file.name })
            columns.add(BaseFileColumn<GitStatusLine>("path", base = tool.directory.value!!) { it.file })
            columns.add(Column<GitStatusLine, String?>("renamedFrom") { it.renamed })
        }

        override fun updateRow(tableRow: CustomTableRow, row: GitStatusLine) {
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