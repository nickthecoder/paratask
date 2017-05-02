package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.gui.project.TerminalResults
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.Command

class TerminalTool(
        override val task: CommandTask,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractTool(task), Stoppable {

    private lateinit var command: Command

    private var results: TerminalResults? = null

    override fun run(values: Values) {
        stop()
        command = task.run(values)
    }

    override fun iconName() = if (task.taskD.name == "") "terminal" else task.taskD.name

    override fun updateResults() {
        val results = TerminalResults(
                command,
                showCommand = showCommand,
                allowInput = allowInput)

        toolPane?.updateResults(results)
        results.node.attached()
        this.results = results
    }

    override fun stop() {
        results?.let { it.stop() }
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
    }

    override fun detaching() {
        super.detaching()
        results?.node?.detaching()
    }

}
