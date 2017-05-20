package uk.co.nickthecoder.paratask.project

class OpenProjectTask() : uk.co.nickthecoder.paratask.AbstractTask() {
    override val taskD = uk.co.nickthecoder.paratask.TaskDescription("Open Project")

    val directory = uk.co.nickthecoder.paratask.parameters.FileParameter(
            "Projects Directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory)

    val name = uk.co.nickthecoder.paratask.parameters.ChoiceParameter<String?>("name", value = null)

    init {
        taskD.addParameters(directory, name)
        updateNameChoices()
        directory.listen { updateNameChoices() }
    }

    fun updateNameChoices() {
        name.clearChoices()
        val dir = directory.value
        if (dir == null) return

        val lister = uk.co.nickthecoder.paratask.util.FileLister(extensions = listOf<String>("json"))
        for (file in lister.listFiles(dir)) {
            val label = file.nameWithoutExtension
            name.choice(label, label, label)
        }
    }

    override fun run() {
        val file = java.io.File(directory.value, name.value + ".json")
        javafx.application.Platform.runLater { ProjectWindow.Companion.load(file) }
    }

}

fun main(args: Array<String>) {
    uk.co.nickthecoder.paratask.TaskParser(OpenProjectTask()).go(args)
}
