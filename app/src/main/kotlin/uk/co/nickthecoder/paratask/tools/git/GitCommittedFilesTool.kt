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

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.WrappedFile
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class GitCommittedFilesTool() : AbstractCommandTool<WrappedFile>(), HasDirectory {

    override val taskD = TaskDescription("gitCommittedFiles", description = "List of File involved in a Git Commit")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory by directoryP

    val commitP = StringParameter("commit")

    val commit by commitP

    constructor(directory: File, commit: String) : this() {
        directoryP.value = directory
        commitP.value = commit
    }

    init {
        taskD.addParameters(directoryP, commitP)
    }


    override fun createColumns() {
        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { ImageView(it.icon) })
        columns.add(BaseFileColumn<WrappedFile>("path", base = directory!!) { it.file })
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("git", "diff-tree", "--no-commit-id", "--name-only", "-r", commit).dir(directory!!)
        return command
    }

    override fun processLine(line: String) {
        list.add(WrappedFile(File(directory, line)))
    }
}
