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

import javafx.application.Platform
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.ResultsTab
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.runAndWait

class SimpleTerminalResults(tool: Tool)

    : TerminalResults, AbstractResults(tool, "Terminal"), Stoppable {

    var showCommand: Boolean = true

    var allowInput: Boolean = false

    constructor(tool: Tool, showCommand: Boolean = true, allowInput: Boolean = false) : this(tool) {
        this.showCommand = showCommand
        this.allowInput = allowInput
    }

    override val node = StackPane()

    private var simpleTerminal: SimpleTerminal? = null

    private var exec: Exec? = null

    override val process
        get() = exec?.process

    override fun start(osCommand: OSCommand) {
        exec = Exec(osCommand)
        runAndWait {
            simpleTerminal = SimpleTerminal(exec!!, showCommand, allowInput)
            simpleTerminal?.start()
            node.children.clear()
            node.children.add(simpleTerminal)
        }
    }

    override fun waitFor(): Int {
        val exitStatus = exec?.waitFor() ?: -12
        Platform.runLater {
            labelProperty.set("Finished (Exit Status=$exitStatus)")
        }
        return exitStatus
    }

    override fun stop() {
        exec?.kill(false)
    }

    override fun detaching() {
        ParaTaskApp.logAttach("TerminalResults.detaching")
        super.detaching()
        simpleTerminal?.detaching()
        ParaTaskApp.logAttach("TerminalResults.detached")
    }

    override fun focus() {
        simpleTerminal?.focus()
    }
}
