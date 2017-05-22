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

import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

class TerminalResults(
        tool: Tool,
        val osCommand: OSCommand,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractResults(tool, "Terminal"), Stoppable {

    val exec = Exec(osCommand)

    override val node = SimpleTerminal(
            exec,
            showCommand = showCommand,
            allowInput = allowInput)

    fun start() {
        node.start()
    }

    fun waitFor(): Int {
        return exec.waitFor()
    }

    override fun stop() {
        exec.kill(false)
    }

    override fun attached(toolPane: ToolPane) {
        ParaTaskApp.logAttach("TerminalResults.attaching")
        super.attached(toolPane)
        node.attached()
        ParaTaskApp.logAttach("TerminalResults.attached")
    }

    override fun detaching() {
        ParaTaskApp.logAttach("TerminalResults.detaching")
        super.detaching()
        node.detaching()
        ParaTaskApp.logAttach("TerminalResults.detached")
    }

    override fun focus() {
        node.focus()
    }
}
