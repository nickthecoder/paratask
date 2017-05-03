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
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractTool(), Stoppable {

    private lateinit var command: Command

    private var results: TerminalResults? = null

    override fun iconName() = if (taskD.name == "") "terminal" else taskD.name

    abstract fun createCommand(values: Values) : Command

    override fun run(values: Values) {
        stop()
        val command = createCommand(values)

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
