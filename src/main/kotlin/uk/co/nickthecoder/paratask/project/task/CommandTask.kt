package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

class CommandTask : AbstractTask() {

    override val taskD = TaskDescription("command", label = "Run an operating system command")

    val commandP = StringParameter("command", value = "bash")

    val argumentsP = MultipleParameter<String>("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    init {
        taskD.addParameters(commandP, argumentsP, directoryP)
    }

    override fun run() {
        val command = Command(commandP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value

        val exec = Exec( command )
        exec.inheritErr()
        exec.inheritOut()
        exec.start()
        exec.waitFor()
    }

}
