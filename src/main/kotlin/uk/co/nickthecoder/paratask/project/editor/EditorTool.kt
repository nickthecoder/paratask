package uk.co.nickthecoder.paratask.project.editor

import javafx.scene.control.TextArea
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.AbstractResults
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import java.io.File

class EditorTool() : AbstractTool() {
    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val filename = MultipleParameter("filename") { FileParameter.factory() }

    constructor(file: File) : this() {
        filename.addValue(file)
    }

    init {
        taskD.addParameters(filename)
    }


    override fun run() {
    }

    override fun updateResults() {
        if (filename.value.size == 0) {
            toolPane?.updateResults(EditorResults(this, null))
        } else {
            val all = filename.value.map { EditorResults(this, it) }
            toolPane?.updateResults(*all.toTypedArray())
        }
    }

    class EditorResults(val tool: EditorTool, val filename: File?) : AbstractResults(filename?.name ?: "New File") {

        override val node = TextArea()

        init {
            node.text = filename?.readText()
            node.styleClass.add("editor")
        }
    }

}

fun main(args: Array<String>) {
    CommandLineTool(EditorTool()).go(args)
}
