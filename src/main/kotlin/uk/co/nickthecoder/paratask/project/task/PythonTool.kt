package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command

class PythonTool : AbstractTerminalTool(allowInput = true, showCommand = true) {

    override val taskD = TaskDescription("python")

    val versionP = StringParameter("version", required = false)

    init {
        taskD.addParameters(versionP)
    }

    override fun createCommand(values: Values): Command {
        return Command("python" + versionP.value(values), "-i")
    }
}

