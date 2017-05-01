package uk.co.nickthecoder.paratask.gui.project

import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

class TerminalResults(val command: Command) : Results {

    override val node = SimpleTerminal(Exec(command))

}
