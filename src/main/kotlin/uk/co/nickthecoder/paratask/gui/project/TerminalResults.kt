package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

class TerminalResults(
        val command: Command,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : Results, Stoppable {

    val exec = Exec(command)

    override val node = SimpleTerminal(
            exec,
            showCommand = showCommand,
            allowInput = allowInput)

    override fun stop() {
        exec.kill(false)
    }
}
