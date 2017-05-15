package uk.co.nickthecoder.paratask.gui.project

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import javafx.application.Platform
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.CommandLineTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class OpenProjectTask() : AbstractTask() {
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
        name.clearChoices()
        val dir = directory.value
        if (dir == null) return

        val lister = FileLister(extensions = listOf<String>("json"))
        for (file in lister.listFiles(dir)) {
            val label = file.nameWithoutExtension
            name.choice(label, label, label)
        }
    }

    override fun run() {
        val file = File(directory.value, name.value + ".json")
        Platform.runLater { ProjectWindow.load(file) }
    }

}

fun main(args: Array<String>) {
    CommandLineTask(OpenProjectTask()).go(args)
}
