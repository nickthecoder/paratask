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
