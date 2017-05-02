package uk.co.nickthecoder.paratask.project.task

import javafx.application.Platform
import uk.co.nickthecoder.paratask.gui.project.TerminalResults
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.runAndWait

abstract class AbstractTerminalTool(
        override val task: AbstractTerminalTask,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractTool(task), Stoppable {

    private lateinit var command: Command

    private var results: TerminalResults? = null

    override fun iconName() = if (task.taskD.name == "") "terminal" else task.taskD.name


    override fun run(values: Values) {
        stop()
        val command = task.command(values)

        runAndWait {
            val results = TerminalResults(command, showCommand = showCommand, allowInput = allowInput)

            toolPane?.updateResults(results)

            this.results = results
        }
        results?.start()
        results?.waitFor()
    }

    override fun updateResults() {
        // We updated the results in run, because run will block till the command ends
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
