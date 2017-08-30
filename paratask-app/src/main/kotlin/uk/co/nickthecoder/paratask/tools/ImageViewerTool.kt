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
package uk.co.nickthecoder.paratask.tools

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.imageExtensions
import java.io.File

class ImageViewerTool() : AbstractTool() {

    constructor(file: File) : this() {
        fileP.value = file
    }

    override val taskD = TaskDescription("imageViewer")

    val fileP = FileParameter("file", expectFile = true, mustExist = true)

    val file by fileP

    init {
        taskD.addParameters(fileP)
    }

    override fun loadProblem(parameterName: String, expression: String?, stringValue: String?) {
        if (parameterName == "image") {
            if (expression == null) {
                fileP.value = File(stringValue)
            } else {
                fileP.expression = expression
            }
            return
        }
        super.loadProblem(parameterName, expression, stringValue)
    }

    override fun createResults() = listOf(ImageResults(fileP.value!!))

    override fun run() {}

    override fun createHeader() = Header(this, fileP)

    val lister = FileLister(onlyFiles = true, extensions = imageExtensions.toList())

    fun nextImage(): ImageViewerTool? {
        lister.nextFile(file!!)?.let {
            return ImageViewerTool(it)
        }
        return null
    }

    fun previousImage(): ImageViewerTool? {
        lister.previousFile(file!!)?.let {
            return ImageViewerTool(it)
        }
        return null
    }

    inner class ImageResults(val file: File) : AbstractResults(this@ImageViewerTool) {

        override val node = BorderPane()

        init {

            val i = file.inputStream()
            with(i) {
                val image = Image(i)
                val iv = WrappedImageView(image)
                node.center = iv
            }
        }

        override fun focus() {
            node.requestFocus()
        }
    }

    inner class WrappedImageView(image: Image) : ImageView(image) {

        init {
            isPreserveRatio = true
        }

        override fun minWidth(height: Double): Double {
            return 40.0
        }

        override fun prefWidth(height: Double): Double {
            val I = image ?: return minWidth(height)
            return I.width
        }

        override fun maxWidth(height: Double): Double {
            return 16384.0
        }

        override fun minHeight(width: Double): Double {
            return 40.0
        }

        override fun prefHeight(width: Double): Double {
            val I = image ?: return minHeight(width)
            return I.height
        }

        override fun maxHeight(width: Double): Double {
            return 16384.0
        }

        override fun isResizable(): Boolean {
            return true
        }

        override fun resize(width: Double, height: Double) {
            fitWidth = width
            fitHeight = height
        }
    }
}
