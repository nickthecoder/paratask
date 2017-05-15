package uk.co.nickthecoder.paratask.gui

import uk.co.nickthecoder.paratask.project.ResultProcessor
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.runAndWait

class CommandInTerminalWindow(val title: String) : ResultProcessor {

    override fun process(result: Any?): Boolean {
        if (result is Command) {

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
