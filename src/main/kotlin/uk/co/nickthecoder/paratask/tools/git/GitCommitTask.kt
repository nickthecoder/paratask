package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File

class GitCommitTask() : AbstractTask() {

    override val taskD = TaskDescription("gitCommit")

    val message = StringParameter("message")

    val all = BooleanParameter("all")

    val directory = FileParameter("directory", expectFile = false)

        constructor(directory: File, all: Boolean = false) : this() {
        this.directory.value = directory
        this.all.value = all
    }

    init {
        taskD.addParameters(message, all, directory)
    }

    override fun run() {
        val exec = Exec("git", "commit", "-m", message.value, if (all.value == true) "-a" else null).dir(directory.value)
        exec.start()
    }
}