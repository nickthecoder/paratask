package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.table.WrappedRow
import uk.co.nickthecoder.paratask.tools.git.GitTool.GitStatusRow

// TODO Allow results to be filtered based on index and work?
// e.g. show changes, deletions, un

class GitTool() : uk.co.nickthecoder.paratask.tools.AbstractCommandTool<GitStatusRow>() {

    override val taskD = uk.co.nickthecoder.paratask.TaskDescription("git", description = "Source Code Control")

    val directoryP = uk.co.nickthecoder.paratask.parameters.FileParameter("directory", expectFile = false)

    val directory: java.io.File? by directoryP

    constructor(directory: java.io.File) : this() {
        directoryP.value = directory
    }

    init {
        taskD.addParameters(directoryP)
    }

    override fun createColumns() {
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitStatusRow, Char>("index") { it.index })
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitStatusRow, Char>("work") { it.work })
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitStatusRow, String>("name") { it.file.name })
        columns.add(uk.co.nickthecoder.paratask.table.BaseFileColumn<GitStatusRow>("path", base = directory!!) { it.file })
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitStatusRow, String?>("renamedFrom") { it.renamed })
    }


    override fun createCommand(): uk.co.nickthecoder.paratask.util.process.OSCommand {
        return uk.co.nickthecoder.paratask.util.process.OSCommand("git", "status", "--porcelain").dir(directory!!)
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
        val file = java.io.File(directory, path)

        val gsr = GitStatusRow(file, index = index, work = work, renamed = renamed)
        list.add(gsr)
    }

    fun addDirectory(directory: java.io.File, index: Char, work: Char) {
        val fileLister = uk.co.nickthecoder.paratask.util.FileLister(depth = 10, includeHidden = true)
        val listing = fileLister.listFiles(directory)
        for (file in listing) {
            list.add(GitStatusRow(file, index = index, work = work))
        }
    }

    override fun updateRow(tableRow: javafx.scene.control.TableRow<WrappedRow<GitStatusRow>>, row: uk.co.nickthecoder.paratask.tools.git.GitTool.GitStatusRow) {
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
            val file: java.io.File,
            val index: Char,
            val work: Char,
            val renamed: String? = null) {

        val path: String

        init {
            val filePath = file.path
            val prefix = directory!!.path + java.io.File.separatorChar
            if (filePath.startsWith(prefix)) {
                path = filePath.substring(prefix.length)
            } else {
                path = filePath
            }
        }
    }
}

fun main(args: Array<String>) {
    ToolParser(GitTool()).go(args)
}