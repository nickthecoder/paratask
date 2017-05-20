package uk.co.nickthecoder.paratask.tools.git

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.WrappedFile
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class GitCommittedFilesTool() : AbstractCommandTool<WrappedFile>() {

    override val taskD = TaskDescription("gitCommittedFiles", description = "List of File involved in a Git Commit")

    val directoryP = FileParameter("directory", expectFile = false)

    val commitP = StringParameter("commit")

    val commit by commitP

    constructor(directory: File, commit: String) : this() {
        directoryP.value = directory
        commitP.value = commit
    }

    init {
        taskD.addParameters(directoryP, commitP)
    }

    val directory: File
        get() = directoryP.value!!


    override fun createColumns() {
        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(BaseFileColumn<WrappedFile>("path", base = directory) { it.file })
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("git", "diff-tree", "--no-commit-id", "--name-only", "-r", commit).dir(directory)
        return command
    }

    override fun processLine(line: String) {
        list.add(WrappedFile(File(directory, line)))
    }
}
