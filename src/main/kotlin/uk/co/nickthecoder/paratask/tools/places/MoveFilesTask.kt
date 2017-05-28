package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

class MoveFilesTask() : AbstractTask() {

    override val taskD = TaskDescription("moveFiles")

    val filesP = MultipleParameter("files", minItems = 1) { FileParameter("file", expectFile = null, mustExist = true) }

    val toDirectoryP = FileParameter("toDirectory", mustExist = true, expectFile = false)

    init {
        taskD.addParameters(filesP, toDirectoryP)
    }

    override fun run() {
        val command = OSCommand("mv", "--", filesP.value, toDirectoryP.value!!)
        Exec(command).start().waitFor()
    }
}
