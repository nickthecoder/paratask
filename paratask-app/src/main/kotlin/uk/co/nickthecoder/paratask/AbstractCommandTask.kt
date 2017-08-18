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
package uk.co.nickthecoder.paratask

import javafx.application.Platform
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.gui.PlainWindow
import uk.co.nickthecoder.paratask.tools.terminal.SimpleTerminal
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.enumChoices
import uk.co.nickthecoder.paratask.project.ProjectWindow
import uk.co.nickthecoder.paratask.tools.ExecTool
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.SimpleSink
import uk.co.nickthecoder.paratask.util.runAndWait

abstract class AbstractCommandTask() : AbstractTask() {

    var command: OSCommand? = null

    var exec: Exec? = null

    enum class Output(override val label: String) : Labelled {
        INHERRIT("Pass through"), WINDOW("New Window"), TOOL("New Tool"), IGNORE("Ignore")
    }

    val outputP = ChoiceParameter<Output>("output", value = Output.INHERRIT).enumChoices()

    var output by outputP

    /*
     * Only used when output is TOOL (so we know which project window to create a new output tool).
     */
    var projectWindow: ProjectWindow? = null

    abstract fun createCommand(): OSCommand

    override fun run() {
        val command = createCommand()
        this.command = command
        val exec = Exec(command)
        this.exec = exec

        when (output) {
            Output.TOOL -> {
                // Must run it through ParaTaskApp, to ensure that JavaFX is started. Grrr.
                ParaTaskApp.runFunction { openTool() }
            }
            Output.WINDOW -> {
                // Must run it through ParaTaskApp, to ensure that JavaFX is started. Grrr.
                ParaTaskApp.runFunction { openSimpleTerminal() }
            }
            Output.INHERRIT -> {
                exec.inheritErr()
                exec.inheritOut()
            }
            Output.IGNORE -> {
                exec.outSink = SimpleSink()
                exec.errSink = SimpleSink()
            }

        }

        exec.start()
        exec.waitFor()
    }

    fun openSimpleTerminal() {
        runAndWait {
            val terminal = SimpleTerminal(exec!!)
            PlainWindow(taskD.label, terminal)
            terminal.start()
        }
    }

    fun openTool() {
        AutoExit.inc("Open ExecTool from ${taskD.label}")
        Platform.runLater {
            val tool = ExecTool(exec!!)

            var projectWindow = this.projectWindow
            if (projectWindow == null) {
                projectWindow = ProjectWindow()
                projectWindow.placeOnStage(Stage())
            }
            projectWindow.addTool(tool)
            AutoExit.dec("Open ExecTool from ${taskD.label}")
        }
    }

}
