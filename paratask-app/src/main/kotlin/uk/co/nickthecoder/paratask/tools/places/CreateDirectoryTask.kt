package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import java.io.File

class CreateDirectoryTask() : AbstractTask() {

    override val taskD = TaskDescription("createDirectory")

    val parentDirectoryP = FileParameter("parentDirectory", expectFile = false, required = true, hidden = true)
    val directoryNameP = StringParameter("directoryName")

    init {
        taskD.addParameters(parentDirectoryP, directoryNameP)
    }

    override fun customCheck() {
        val directory = File(parentDirectoryP.value!!, directoryNameP.value)
        if (directory.exists()) {
            throw ParameterException(directoryNameP, "Already exists")
        }
    }

    override fun run() {
        val directory = File(parentDirectoryP.value!!, directoryNameP.value)
        directory.mkdir()
    }

}
