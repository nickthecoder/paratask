/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.project.TaskPrompter

class ParaTaskApp : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return
        }
        TaskPrompter(task).placeOnStage(stage)
    }

    companion object {
        lateinit var task: Task

        private val imageMap = mutableMapOf<String, Image?>()

        fun style(scene: Scene) {
            val resource = ParaTaskApp::class.java.getResource("paratask.css")
            scene.stylesheets.add(resource.toExternalForm())
        }

        fun imageResource(name: String): Image? {
            val image = imageMap[name]
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