package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool() : AbstractTerminalTool(showCommand = true, allowInput = true) {

    override val taskD = TaskDescription("terminal")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = StringParameter("arguments", required = false).multiple()

    init {
        taskD.addParameters(commandP, argumentsP)
    }

    override fun createCommand(values: Values): Command {
        val command = Command(commandP.value(values))
        argumentsP.list(values).forEach { arg ->
            command.addArgument(arg)
        }
        return command
    }

}
