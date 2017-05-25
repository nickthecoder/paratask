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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File

class GitCommitTask() : AbstractTask(), HasDirectory {

    override val taskD = TaskDescription("gitCommit")

    val messageP = StringParameter("message", columns = 60, rows = 10)

    val allP = BooleanParameter("all")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory by directoryP

    constructor(directory: File, all: Boolean = false) : this() {
        this.directoryP.value = directory
        this.allP.value = all
    }

    init {
        taskD.addParameters(messageP, allP, directoryP)
    }

    override fun run() {
        val exec = Exec("git", "commit", "-m", messageP.value, if (allP.value == true) "-a" else null).dir(directoryP.value)
        exec.start()
    }
}