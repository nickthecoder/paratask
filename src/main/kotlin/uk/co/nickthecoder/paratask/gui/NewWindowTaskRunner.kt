package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.runAndWait

class NewWindowTaskRunner(val title: String) : TaskRunner() {

    override fun processCommand(command: Command) {
        val exec = Exec(command)

        runAndWait {
            val terminal = SimpleTerminal(exec)

            PlainWindow("${title}", terminal)
            terminal.inputField.requestFocus()
            exec.start()
        }
    }
}