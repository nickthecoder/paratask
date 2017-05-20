package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand

class PythonTool : AbstractTerminalTool(allowInput = true, showCommand = true) {

    override val taskD = TaskDescription("python", description="Interactive Python Shell")

    val versionP = StringParameter("version", required = false)

    init {
        taskD.addParameters(versionP)
    }

    override fun createCommand(): OSCommand {
        return OSCommand("python" + versionP.value, "-i")
    }
}

