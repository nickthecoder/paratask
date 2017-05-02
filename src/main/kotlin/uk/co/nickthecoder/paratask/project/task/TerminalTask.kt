package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class TerminalTask : AbstractTerminalTask() {

    override val taskD = TaskDescription("terminal")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = StringParameter("arguments", required = false).multiple()

    init {
        taskD.addParameters(commandP, argumentsP)
    }

    override fun command(values: Values): Command {
        val command = Command(commandP.value(values))
        argumentsP.list(values).forEach { arg ->
            command.addArgument(arg)
        }
        return command
    }

    override fun check(values: Values) {}
}