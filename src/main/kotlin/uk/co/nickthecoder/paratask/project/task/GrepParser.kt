package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values
import java.io.File

/**
 * Converts the text output from GrepTask's into a List, so that the results can be displayed as a table.
 */
class GrepParser : Task {

    val grepTask = GrepTask()

    override val taskD = grepTask.taskD

    override fun run(values: Values): List<GrepRow> {
        val result = mutableListOf<GrepRow>()

        val exec = grepTask.run(values)
        // TODO Parse the output of the Exec

        return result
    }

    override fun check(values: Values) {
        grepTask.check(values)
    }

    // TODO GrepRow will probably inherit from HasFile later on
    data class GrepRow(val file: File, var lineNumber: Int, var line: String) {

    }
}
