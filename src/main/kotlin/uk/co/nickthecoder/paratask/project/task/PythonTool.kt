package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Command

class PythonTool : AbstractTerminalTool(allowInput = true, showCommand = true) {

    override val taskD = TaskDescription("python", description="Interactive Python Shell")

    val versionP = StringParameter("version", required = false)

    init {
        taskD.addParameters(versionP)
    }

    override fun createCommand(): Command {
        return Command("python" + versionP.value, "-i")
    }
}

