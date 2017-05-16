package uk.co.nickthecoder.paratask.project.task

import javafx.scene.control.TableRow
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.BaseFileColumn
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.table.WrappedRow
import uk.co.nickthecoder.paratask.project.task.GitTool.GitStatusRow
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.HasFile
import java.io.File

// TODO Allow results to be filtered based on index and work?
// e.g. show changes, deletions, un

class GitTool() : AbstractCommandTool<GitStatusRow>(), HasDirectory {

    override val taskD = TaskDescription("git", description = "Source Code Control")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory: File
        get() = directoryP.value!!

    constructor(directory: File) : this() {
        directoryP.value = directory
    }

    init {
        taskD.addParameters(directoryP)
    }

    override fun createColumns() {
        columns.add(Column<GitStatusRow, Char>("index") { it.index })
        columns.add(Column<GitStatusRow, Char>("work") { it.work })
        columns.add(Column<GitStatusRow, String>("name") { it.file.name })
        columns.add(BaseFileColumn<GitStatusRow>("path", base = directory) { it.file })
        columns.add(Column<GitStatusRow, String?>("renamedFrom") { it.renamed })
    }


    override fun createCommand() : Command {
        return Command("git", "status", "--porcelain").dir(directory)
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
        list.add(gsr)
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
        tableRow.getStyleClass().add("git-" + style)
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
            val prefix = directory.path + File.separatorChar
            if (filePath.startsWith(prefix)) {
                path = filePath.substring(prefix.length)
            } else {
                path = filePath
            }
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTool(GitTool()).go(args)
}