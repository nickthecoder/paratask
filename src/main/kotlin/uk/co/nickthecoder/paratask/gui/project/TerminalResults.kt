package uk.co.nickthecoder.paratask.gui.project

import javafx.scene.Node
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

class TerminalResults(
        val command: Command,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : EmptyResults(), Stoppable {

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

    override fun chooseFocus(toolPane: ToolPane): Node {
        return node.chooseFocus() ?: super.chooseFocus(toolPane)
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        node.attached()
    }

    override fun detaching() {
        node.detaching()
        node.detaching()
    }
}
