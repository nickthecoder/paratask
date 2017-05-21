package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class GitRMTask() : AbstractTask() {
    override val taskD = TaskDescription("gitRM")

    val directoryP = FileParameter("directory", expectFile = false)

    val filesP = MultipleParameter<File?>("files", minItems = 1) { FileParameter("", expectFile = null, baseDirectoryP = directoryP) }

    val optionP = ChoiceParameter<String?>("option", value = null, required = false)
            .choice("none", null, "<default>")
            .choice("-f", "-f", "Force Removal")
            .choice("--cache", "--cache", "Keep File (remove from cache)")

    init {
        taskD.addParameters(directoryP, filesP, optionP)
    }

    override fun run(): OSCommand {
        val command = OSCommand("git", "rm", optionP.value, "--", filesP.stringValue).dir(directoryP.value!!)

        return command
    }
}
