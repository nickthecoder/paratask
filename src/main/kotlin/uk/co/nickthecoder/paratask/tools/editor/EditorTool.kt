package uk.co.nickthecoder.paratask.tools.editor

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.ToolParser
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
        if (fileP.value.isEmpty()) {
            return singleResults(EditorResults(this, null))
        } else {
            return fileP.value.map { EditorResults(this, it) }
        }
    }

}

fun main(args: Array<String>) {
    ToolParser(EditorTool()).go(args)
}
