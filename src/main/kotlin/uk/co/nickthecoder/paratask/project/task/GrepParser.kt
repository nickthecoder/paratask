package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import java.io.File

/**
 * Converts the text output from GrepTask's into a List, so that the results can be displayed as a table.
 */
class GrepParser : AbstractTask() {

    val grepTask = GrepTask()

    override val taskD = grepTask.taskD

    override fun run(): List<GrepRow> {
        val result = mutableListOf<GrepRow>()

        grepTask.run()
        // TODO Parse the output of the Exec

        return result
    }

    override fun check() {
        grepTask.check()
    }

    // TODO GrepRow will probably inherit from HasFile later on
    data class GrepRow(val file: File, var lineNumber: Int, var line: String) {

    }
}
