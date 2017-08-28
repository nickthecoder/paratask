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
package uk.co.nickthecoder.paratask.tools.terminal

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand


class SSHTool : AbstractTerminalTool() {

    override val taskD = TaskDescription("ssh", description = "Secure Shell")

    val userP = StringParameter("user", value = "", required = false)

    val hostP = StringParameter("host")

    val xP = BooleanParameter("xForwarding", value = true)

    val commandP = StringParameter("command", required = false)

    init {
        taskD.addParameters(userP, hostP, xP, commandP)
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("ssh")
        if (xP.value == true) {
            command.addArgument("-X")
        }
        if (userP.value.isNotBlank()) {
            command.addArgument("${userP.value}@${hostP.value}")
        } else {
            command.addArgument(hostP.value)
        }
        if (commandP.value.isNotEmpty()) {
            command.addArgument(commandP.value)
        }

        return command
    }

}
