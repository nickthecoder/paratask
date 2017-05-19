package uk.co.nickthecoder.paratask.project.editor

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.Results
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import java.io.File

class EditorTool() : AbstractTool() {

    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val fileP = MultipleParameter("file") { FileParameter("") }

    constructor(vararg files: File) : this() {
        for (file in files) {
            fileP.addValue(file)
        }
    }

    constructor(files: List<File>) : this() {
        for (file in files) {
            fileP.addValue(file)
        }
    }

    init {
        taskD.addParameters(fileP)
    }


    override fun run() {
    }

    override fun createResults(): List<Results> {
        if (fileP.value.size == 0) {
            return singleResults(EditorResults(this, null))
        } else {
            return fileP.value.map { EditorResults(this, it) }
        }
    }

}

fun main(args: Array<String>) {
    CommandLineTool(EditorTool()).go(args)
}
