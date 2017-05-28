package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

class RenameFileTask() : AbstractTask() {

    override val taskD = TaskDescription("renameFile")

    val fromP = FileParameter("from", mustExist = true)

    val toP = FileParameter("to", mustExist = false)

    init {
        taskD.addParameters(fromP, toP)
    }

    override fun run() {
        val command = OSCommand("mv", "--", fromP.value, toP.value!!)
        Exec(command).start().waitFor()
    }
}
