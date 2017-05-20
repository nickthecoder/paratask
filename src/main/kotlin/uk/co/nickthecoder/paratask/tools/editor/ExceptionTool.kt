package uk.co.nickthecoder.paratask.tools.editor

import javafx.scene.control.TextArea
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.AbstractTool
import java.io.PrintWriter
import java.io.StringWriter

class ExceptionTool : AbstractTool {

    override val taskD = TaskDescription("error", description = "An error has occurred")

    val exception: Exception?

    constructor() : super() {
        exception = null
    }

    constructor(e: Exception) : super() {
        e.printStackTrace()
        exception = e
    }

    override fun run() {
    }

    override fun createResults() = singleResults(ExceptionResults())

    inner class ExceptionResults : AbstractResults(this@ExceptionTool) {

        override val node = TextArea()

        init {
            val writer = StringWriter()
            exception?.printStackTrace(PrintWriter(writer))
            node.text = exception?.message + "\n\n" + writer.toString()

        }
    }
}
