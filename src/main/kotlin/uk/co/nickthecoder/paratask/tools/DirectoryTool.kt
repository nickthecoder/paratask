package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.ToolParser
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
    ToolParser(DirectoryTool()).go(args)
}
