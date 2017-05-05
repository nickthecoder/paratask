package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.MultipleValue
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool() : AbstractTerminalTool(showCommand = true, allowInput = true) {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = StringParameter("arguments", required = false).multiple()

    init {
        taskD.addParameters(commandP, argumentsP)
    }

    fun changeCommand(command: Command) {
        val arguments: MultipleValue<String> = argumentsP.parameterValue(values)
        arguments.clear()
        for (i in command.command.indices) {
            if (i == 0) {
                commandP.set(values, command.command[0])
            } else {
                arguments.addItem(command.command[i])
            }
        }
    }

    override fun createCommand(values: Values): Command {
        val command = Command(commandP.value(values))
        argumentsP.list(values).forEach { arg ->
            command.addArgument(arg)
        }
        return command
    }

}
