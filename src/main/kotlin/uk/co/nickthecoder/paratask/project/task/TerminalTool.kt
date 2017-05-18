package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool : AbstractTerminalTool {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = MultipleParameter<String>("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    constructor() : super(showCommand = true, allowInput = true) {
    }

    constructor(command: Command) : this() {
        commandP.value = command.program
        argumentsP.value = command.arguments
        directoryP.value = command.directory
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

    fun changeCommand(command: Command) {
        argumentsP.clear()
        for (i in command.command.indices) {
            if (i == 0) {
                commandP.value = command.command[0]
            } else {
                argumentsP.addValue(command.command[i])
            }
        }
    }

    override fun createCommand(): Command {
        val command = Command(commandP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value
        return command
    }

}
