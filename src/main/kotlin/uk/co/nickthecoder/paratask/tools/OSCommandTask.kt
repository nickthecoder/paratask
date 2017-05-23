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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

class OSCommandTask : AbstractTask() {

    override val taskD = TaskDescription("osCommand", label = "Run an Operating System Command")

    val commandP = StringParameter("osCommand", value = "bash")

    val argumentsP = MultipleParameter("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    init {
        taskD.addParameters(commandP, argumentsP, directoryP)
    }

    override fun run() {
        val command = OSCommand(commandP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value

        val exec = Exec( command )
        exec.inheritErr()
        exec.inheritOut()
        exec.start()
        exec.waitFor()
    }

}
