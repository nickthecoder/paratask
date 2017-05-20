package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.project.ProjectWindow
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.project.TaskRunner
import uk.co.nickthecoder.paratask.project.UnthreadedTaskRunner
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.File

class StartTask : AbstractTask() {

    override val taskD = TaskDescription("Open Project")

    override var taskRunner: TaskRunner = UnthreadedTaskRunner(this)

    val directoryP = FileParameter(
            "directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory,
            description = "The directory containing the project files\ndefault=${Preferences.projectsDirectory}")

    val projectsP = MultipleParameter("projects", minItems = 1,
            description = "The names of the projects to load. (Do not include the .json suffix)")
    { StringParameter("") }

    init {
        taskD.addParameters(directoryP, projectsP)
        taskD.unnamedParameter = projectsP
    }

    override fun run() {
        val projectFiles = projectsP.value.map {
            File(directoryP.value, it + ".json")
        }
        StartingApp.projectFiles = projectFiles
        Application.launch(StartingApp::class.java)
    }
}

// Wow, what a bloody palavor it is to lauch JavaFX. I'm sure its fine when you want a single window
// with no osCommand line arguments, and no possibility that JavaFX isn't needed, but for paratask,
// it is bloody horrible!

class StartingApp : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return
        }
        val label = Label("")
        val scene = Scene(label)
        stage.title = "Loading ParaTask"
        stage.scene = scene
        AutoExit.show(stage)

        for (file in projectFiles) {
            ProjectWindow.load(file)
        }

        stage.close()
    }


    companion object {
        lateinit var projectFiles: List<File>
    }
}

fun main(args: Array<String>) {
    TaskParser(StartTask()).go(args)
}
