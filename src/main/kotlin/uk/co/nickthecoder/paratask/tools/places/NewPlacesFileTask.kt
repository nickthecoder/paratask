package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File

class NewPlacesFileTask() : AbstractTask() {

    override val taskD = TaskDescription("createNewPlacesFile")

    val directoryP = FileParameter("directory", mustExist = true, expectFile = false)

    val filenameP = StringParameter("filename")

    init {
        taskD.addParameters(directoryP, filenameP)
    }

    constructor(directory: File) : this() {
        directoryP.value = directory
    }

    override fun run() {
        val file = File(directoryP.value!!, filenameP.value)
        file.writeText("")
    }
}
