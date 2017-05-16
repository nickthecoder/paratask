package uk.co.nickthecoder.paratask.project.task

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.table.BaseFileColumn
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.WrappedFile
import java.io.File

class GitCommittedFilesTool() : AbstractCommandTool<WrappedFile>(), HasDirectory {

    override val taskD = TaskDescription("gitCommittedFiles", description = "List of File involved in a Git Commit")

    val directoryP = FileParameter("directory", expectFile = false)

    val commitP = StringParameter("commit")

    val commit: String
        get() = commitP.value

    constructor(directory: File, commit: String) : this() {
        directoryP.value = directory
        commitP.value = commit
    }

    init {
        taskD.addParameters(directoryP, commitP)
    }

    override val directory: File
        get() = directoryP.value!!


    override fun createColumns() {
        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(BaseFileColumn<WrappedFile>("path", base = directory) { it.file })
    }

    override fun createCommand(): Command {
        val command = Command("git", "diff-tree", "--no-commit-id", "--name-only", "-r", commit).dir(directory)
        return command
    }

    override fun processLine(line: String) {
        list.add(WrappedFile(File(directory, line)))
    }
}
