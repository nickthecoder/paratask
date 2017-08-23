package uk.co.nickthecoder.paratask.tools.terminal

import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand

interface TerminalResults : Results, Stoppable {

    fun start(osCommand: OSCommand)

    fun waitFor(): Int

    val process : Process?
}
