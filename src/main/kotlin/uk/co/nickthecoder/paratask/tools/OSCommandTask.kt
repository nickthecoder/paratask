package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

class OSCommandTask : AbstractTask() {

    override val taskD = TaskDescription("osCommand", label = "Run an operating system osCommand")

    val commandP = StringParameter("osCommand", value = "bash")

    val argumentsP = MultipleParameter<String>("arguments") { StringParameter("") }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    init {
        taskD.addParameters(commandP, argumentsP, directoryP)
    }

    override fun run() {
        val command = OSCommand(commandP.value)
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
