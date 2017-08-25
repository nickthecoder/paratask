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

package uk.co.nickthecoder.paratask.tools.terminal

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.OSCommand

class TerminalTool : AbstractTerminalTool(showCommand = true, allowInput = true) {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val programP = StringParameter("program", value = "bash")

    val argumentsP = MultipleParameter("arguments") { StringParameter("", required = false) }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    val closeWhenFinishedP = BooleanParameter("closeWhenFinished", value = true)

    init {
        taskD.addParameters(programP, argumentsP, directoryP, closeWhenFinishedP)
    }

    override fun finished() {
        if (closeWhenFinishedP.value == true) {
            toolPane?.halfTab?.close()
        }
    }

    fun input(value: Boolean): TerminalTool {
        allowInput = value
        return this
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand(programP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value
        return command
    }

}
