package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class DirectoryTreeTool() : AbstractDirectoryTool("directoryTree", "Work with a Directory Tree") {

    constructor(directory: File) : this() {
        this.directoryP.value = directory
    }

    override val optionsName = "directory"

    init {
        depthP.value = 3
    }

    override fun isTree(): Boolean = true

}

fun main(args: Array<String>) {
    CommandLineTool(DirectoryTreeTool()).go(args)
}
