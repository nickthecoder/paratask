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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.util.HasDirectory
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
        val command = createCommand()

        runAndWait {
            terminalResults = TerminalResults(this, command, showCommand = showCommand, allowInput = allowInput)

            toolPane?.replaceResults(createResults(),resultsList)

        }
        terminalResults?.start()
        terminalResults?.waitFor()
    }

    override fun updateResults() {
        // We updated the results in run, because run will block till the osCommand ends
    }

    override fun createResults(): List<Results> = singleResults(terminalResults!!)

    fun getOutput() :String {
        return terminalResults?.simpleTerminal?.textArea?.text ?: ""
    }

    override fun stop() {
        terminalResults?.stop()
    }
}
