package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.gui.project.TerminalResults
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.tasks.GrepTask
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool(override val task: CommandTask) : AbstractTool(task) {

    lateinit var command: Command

    override fun run(values: Values) {
        command = task.run(values)
    }

    override fun createResults(): TerminalResults {
        return TerminalResults(command)
    }
}

// TODO Remove this once we can test Grep as a tool using GrepTask as the entry point.
fun main(args: Array<String>) {
    CommandLineTool(TerminalTool(GrepTask())).go(args)
}
