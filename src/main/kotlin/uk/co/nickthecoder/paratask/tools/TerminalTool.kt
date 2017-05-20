package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand

class TerminalTool : AbstractTerminalTool {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val commandP = StringParameter("osCommand", value = "bash")

    val argumentsP = MultipleParameter<String>("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    constructor() : super(showCommand = true, allowInput = true) {
    }

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

    fun changeCommand(osCommand: OSCommand) {
        argumentsP.clear()
        for (i in osCommand.command.indices) {
            if (i == 0) {
                commandP.value = osCommand.command[0]
            } else {
                argumentsP.addValue(osCommand.command[i])
            }
        }
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
