package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter

class ParaTaskApp() : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return;
        }
        TaskPrompter(task, task.taskD.createValues()).placeOnStage(stage)
    }

    companion object {
        lateinit var task: Task

        fun style(scene: Scene) {
            val resource = ParaTaskApp::class.java.getResource("paratask.css")
            scene.getStylesheets().add(resource.toExternalForm())
        }

    }

}