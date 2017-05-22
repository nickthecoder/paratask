package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

class TerminalResults(
        tool: Tool,
        val osCommand: OSCommand,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractResults(tool, "Terminal"), Stoppable {

    val exec = Exec(osCommand)

    override val node = SimpleTerminal(
            exec,
            showCommand = showCommand,
            allowInput = allowInput)

    fun start() {
        node.start()
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

    override fun focus() {
        node.focus()
    }
}
