package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class OpenProjectTask : AbstractTask() {
    override val taskD = TaskDescription("Open Project")

    val directory = FileParameter(
            "Projects Directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory)

    val name = ChoiceParameter<String?>("name", value = null)

    init {
        taskD.addParameters(directory, name)
        updateNameChoices()
        directory.listen { updateNameChoices() }
    }

    fun updateNameChoices() {
        name.clear()
        val dir = directory.value ?: return

        val lister = FileLister(extensions = listOf("json"))
        for (file in lister.listFiles(dir)) {
            val label = file.nameWithoutExtension
            name.choice(label, label, label)
        }
    }

    override fun run() {
        val file = File(directory.value, name.value + ".json")
        Platform.runLater { ProjectWindow.Companion.load(file) }
    }

}

fun main(args: Array<String>) {
    TaskParser(OpenProjectTask()).go(args)
}
