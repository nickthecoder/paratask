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

    val lister = FileLister(onlyFiles = true, extensions = listOf("jpg", "jpeg", "png", "bmp"))

    fun nextFile(): File? {
        val files = lister.listFiles(file!!.parentFile)

        var found = false
        for (f in files) {
            if (f == file) {
                found = true
            } else if (found) {
                return f
            }
        }
        return null
    }

    fun previousFile(): File? {
        val files = lister.listFiles(file!!.parentFile)

        var found: File? = null
        for (f in files) {
            if (f == file) {
                return found
            }
            found = f
        }
        return null
    }

    fun nextImage(): ImageViewerTool? {
        nextFile()?.let {
            return ImageViewerTool(it)
        }
        return null
    }

    fun previousImage(): ImageViewerTool? {
        previousFile()?.let {
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
