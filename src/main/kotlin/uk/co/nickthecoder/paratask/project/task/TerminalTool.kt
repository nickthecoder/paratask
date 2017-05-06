package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool() : AbstractTerminalTool(showCommand = true, allowInput = true) {

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = StringParameter("arguments", required = false).multiple()

    init {
        taskD.addParameters(commandP, argumentsP)
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
        return command
    }

}
