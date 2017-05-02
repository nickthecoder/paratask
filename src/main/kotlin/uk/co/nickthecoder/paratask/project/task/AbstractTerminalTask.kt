package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

abstract class AbstractTerminalTask : SimpleTask() {

    override fun run(values: Values) = Unit // Does nothing. The command will be run by the Tool

    abstract fun command(values: Values): Command
}