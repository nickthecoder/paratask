package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.fields.HeaderRow
import java.io.File

class DirectoryTreeTool() : AbstractDirectoryTool("directoryTree", "Work with a Directory Tree") {

    constructor(directory: File) : this() {
        this.directoryP.value = directory
    }

    override val optionsName = "directory"

    init {
        depthP.value = 3
    }

    override fun createHeaderRows(): List<HeaderRow> = listOf(HeaderRow().addAll(directoryP, depthP))

    override fun isTree(): Boolean = true

}

fun main(args: Array<String>) {
    ToolParser(DirectoryTreeTool()).go(args)
}
