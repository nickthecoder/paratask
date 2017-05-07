package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.gui.project.AbstractResults
import uk.co.nickthecoder.paratask.gui.project.ToolPane
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

class TerminalResults(
        val command: Command,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractResults("Terminal"), Stoppable {

    val exec = Exec(command)

    override val node = SimpleTerminal(
            exec,
            showCommand = showCommand,
            allowInput = allowInput)

    fun start() {
        node.start();
    }

    fun waitFor(): Int {
        return exec.waitFor()
    }

    override fun stop() {
        exec.kill(false)
    }

    override fun attached(toolPane: ToolPane) {
        ParaTaskApp.logAttach("TerminalResults.attaching")
        super.attached(toolPane)
        node.attached()
        ParaTaskApp.logAttach("TerminalResults.attached")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("TerminalResults.detaching")
        super.detaching()
        node.detaching()
        ParaTaskApp.logAttach("TerminalResults.detached")
    }
}
