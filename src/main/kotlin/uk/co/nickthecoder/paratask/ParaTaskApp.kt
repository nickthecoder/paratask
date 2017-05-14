package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.TaskPrompter

class ParaTaskApp() : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return;
        }
        TaskPrompter(task).placeOnStage(stage)
    }

    companion object {
        lateinit var task: Task

        private val imageMap = mutableMapOf<String, Image?>()

        fun style(scene: Scene) {
            val resource = ParaTaskApp::class.java.getResource("paratask.css")
            scene.getStylesheets().add(resource.toExternalForm())
        }

        fun imageResource(name: String): Image? {
            val image = imageMap.get(name)
            if (image == null) {
                val imageStream = ParaTaskApp::class.java.getResourceAsStream(name)
                val newImage = if (imageStream == null) null else Image(imageStream)
                imageMap.put(name, newImage)
                return newImage
            }
            return image
        }

        fun logAttach(@Suppress("UNUSED_PARAMETER") string: String) {
            // println( string )
        }
    }

}