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

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class TerminalTool : AbstractTerminalTool {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val commandP = StringParameter("osCommand", value = "bash")

    val argumentsP = MultipleParameter("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    constructor() : super(showCommand = true, allowInput = true)

    constructor(osCommand: OSCommand) : this() {
        commandP.value = osCommand.program
        argumentsP.value = osCommand.arguments
        directoryP.value = osCommand.directory
    }

    constructor(program: String, vararg arguments: Any?) : this() {
        commandP.value = program
        argumentsP.value = arguments.filter { it != null }.map { it.toString() }
    }

    init {
        taskD.addParameters(commandP, argumentsP, directoryP)
    }

    fun input(value: Boolean): TerminalTool {
        allowInput = value
        return this
    }

    fun showCommand(value: Boolean): TerminalTool {
        showCommand = value
        return this
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand(commandP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value
        return command
    }

}
