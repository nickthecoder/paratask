package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Exec
import java.io.File

class GitCommitTask() : AbstractTask() {

    override val taskD = TaskDescription("gitCommit")

    val directory = FileParameter("directory")

    val message = StringParameter("message")

    val all = BooleanParameter("all")

    constructor(directory: File, all: Boolean = false) : this() {
        this.directory.value = directory
        this.all.value = all
    }

    init {
        taskD.addParameters(directory, message, all)
    }

    override fun run() {
        val exec = Exec("git", "commit", "-m", message.value, if (all.value == true) "-a" else null).dir(directory.value)
        exec.start()
    }
}