package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.runAndWait

class TerminalWindowTaskRunner(task: Task, val title: String) : CommandTaskRunner(task) {

    override fun processCommand(command: Command) {
        val exec = Exec(command)

        runAndWait {
            val terminal = SimpleTerminal(exec)

            PlainWindow("${title}", terminal)
        }
    }
}