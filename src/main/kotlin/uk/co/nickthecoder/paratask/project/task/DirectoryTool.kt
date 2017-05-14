package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class DirectoryTool() : AbstractDirectoryTool("directory", "Work with a Single Directory") {

    constructor(directory: File) : this() {
        this.directoryP.value = directory
    }

    init {
        depthP.hidden = true
    }

    override fun isTree(): Boolean = true

}

fun main(args: Array<String>) {
    CommandLineTool(DirectoryTool()).go(args)
}
