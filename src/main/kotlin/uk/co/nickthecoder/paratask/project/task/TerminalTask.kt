package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class TerminalTask : CommandTask {

    override val taskD = TaskDescription("Terminal")

    val commandP = StringParameter("command", value="bash")

    val argumentsP = StringParameter("arguments", required = false).multiple()

    init {
        taskD.addParameters(commandP, argumentsP)
    }

    override fun run(values: Values): Command {
        val command = Command(commandP.value(values))
        argumentsP.list(values).forEach { arg ->
            command.addArgument(arg)
        }

        return command
    }

    override fun check(values: Values) {}
}