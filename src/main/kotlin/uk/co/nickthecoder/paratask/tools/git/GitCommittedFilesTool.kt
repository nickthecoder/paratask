package uk.co.nickthecoder.paratask.tools.git

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.util.WrappedFile

class GitCommittedFilesTool() : uk.co.nickthecoder.paratask.tools.AbstractCommandTool<WrappedFile>() {

    override val taskD = uk.co.nickthecoder.paratask.TaskDescription("gitCommittedFiles", description = "List of File involved in a Git Commit")

    val directoryP = uk.co.nickthecoder.paratask.parameters.FileParameter("directory", expectFile = false)

    val commitP = uk.co.nickthecoder.paratask.parameters.StringParameter("commit")

    val commit by commitP

    constructor(directory: java.io.File, commit: String) : this() {
        directoryP.value = directory
        commitP.value = commit
    }

    init {
        taskD.addParameters(directoryP, commitP)
    }

    val directory: java.io.File
        get() = directoryP.value!!


    override fun createColumns() {
        columns.add(uk.co.nickthecoder.paratask.table.Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(uk.co.nickthecoder.paratask.table.BaseFileColumn<WrappedFile>("path", base = directory) { it.file })
    }

    override fun createCommand(): uk.co.nickthecoder.paratask.util.process.OSCommand {
        val command = uk.co.nickthecoder.paratask.util.process.OSCommand("git", "diff-tree", "--no-commit-id", "--name-only", "-r", commit).dir(directory)
        return command
    }

    override fun processLine(line: String) {
        list.add(uk.co.nickthecoder.paratask.util.WrappedFile(java.io.File(directory, line)))
    }
}
