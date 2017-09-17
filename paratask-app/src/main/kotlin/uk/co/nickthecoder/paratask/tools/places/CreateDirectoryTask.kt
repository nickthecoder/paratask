/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import java.io.File

class CreateDirectoryTask : AbstractTask() {

    override val taskD = TaskDescription("createDirectory")

    val parentDirectoryP = FileParameter("parentDirectory", expectFile = false, required = true)
    val directoryNameP = StringParameter("directoryName")

    init {
        parentDirectoryP.hidden = true
        taskD.addParameters(parentDirectoryP, directoryNameP)
    }

    override fun customCheck() {
        val directory = File(parentDirectoryP.value!!, directoryNameP.value)
        if (directory.exists()) {
            throw ParameterException(directoryNameP, "Already exists")
        }
    }

    override fun run() {
        val directory = File(parentDirectoryP.value!!, directoryNameP.value)
        directory.mkdir()
    }

}
