/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.tools.terminal

import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.runAndWait

abstract class AbstractTerminalTool(
        var showCommand: Boolean = true,
        var allowInput: Boolean = false)

    : AbstractTool(), Stoppable {

    private var terminalResults: TerminalResults? = null

    override fun iconName() = if (taskD.name == "") "terminal" else taskD.name

    abstract fun createCommand(): OSCommand

    override fun run() {
        stop()

        terminalResults = createTerminalResults()
        runAndWait {
            updateResults()
            //toolPane?.replaceResults(createResults(), resultsList)
        }
        val command = createCommand()
        terminalResults?.start(command)
        terminalResults?.waitFor()

        // TODO Do we want to clear the results when the process finishes?
        // Maybe make this an option.
        terminalResults = null
    }

    private fun createTerminalResults(): TerminalResults {
        return SimpleTerminalResults(this, showCommand = showCommand, allowInput = allowInput)
        //return RealTerminalResults(this)
    }

    override fun createResults(): List<Results> {
        return singleResults(terminalResults)
    }

    override fun stop() {
        terminalResults?.stop()
    }
}
