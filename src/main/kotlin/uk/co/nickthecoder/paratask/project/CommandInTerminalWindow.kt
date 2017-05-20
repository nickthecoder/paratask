package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.gui.PlainWindow
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.runAndWait

class CommandInTerminalWindow(val title: String) : ResultProcessor {

    override fun process(result: Any?): Boolean {
        if (result is OSCommand) {

            val exec = Exec(result)

            runAndWait {
                val terminal = SimpleTerminal(exec)
                PlainWindow(title, terminal)
            }
            exec.start()
            return true
        }
        return false
    }
}
