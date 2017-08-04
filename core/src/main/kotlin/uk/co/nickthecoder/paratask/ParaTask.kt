package uk.co.nickthecoder.paratask

import javafx.scene.Scene
import javafx.scene.image.Image

object ParaTask {

    private val imageMap = mutableMapOf<String, Image?>()

    fun style(scene: Scene) {
        val resource = ParaTask::class.java.getResource("paratask.css")
        scene.stylesheets.add(resource.toExternalForm())
    }

    fun imageResource(name: String): Image? {
        val image = imageMap[name]
        if (image == null) {
            val imageStream = ParaTask::class.java.getResourceAsStream(name)
            val newImage = if (imageStream == null) null else Image(imageStream)
            imageMap.put(name, newImage)
            return newImage
        }
        return image
    }
}
