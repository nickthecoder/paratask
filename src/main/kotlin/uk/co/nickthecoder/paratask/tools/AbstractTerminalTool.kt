package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.runAndWait

abstract class AbstractTerminalTool(
        var showCommand: Boolean = true,
        var allowInput: Boolean = false)

    : AbstractTool(), Stoppable {

    private var terminalResults: TerminalResults? = null

    override fun iconName() = if (taskD.name == "") "terminal" else taskD.name

    abstract fun createCommand(): OSCommand

    override fun run() {
        stop()
        val command = createCommand()

        runAndWait {
            terminalResults = TerminalResults(this, command, showCommand = showCommand, allowInput = allowInput)

            toolPane?.replaceResults(createResults(),resultsList)

        }
        terminalResults?.start()
        terminalResults?.waitFor()
    }

    override fun updateResults() {
        // We updated the results in run, because run will block till the osCommand ends
    }

    override fun createResults(): List<Results> = singleResults(terminalResults!!)

    override fun stop() {
        terminalResults?.stop()
    }
}
