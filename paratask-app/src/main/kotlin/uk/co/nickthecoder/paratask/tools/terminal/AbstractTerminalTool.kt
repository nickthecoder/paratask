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

import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.linuxCurrentDirectory
import uk.co.nickthecoder.paratask.util.runAndWait
import java.io.File

abstract class AbstractTerminalTool(
        var showCommand: Boolean = true,
        var allowInput: Boolean = false)

    : AbstractTool(), Stoppable, HasDirectory {

    private var terminalResults: TerminalResults? = null

    override fun iconName() = if (taskD.name == "") "terminal" else taskD.name

    abstract fun createCommand(): OSCommand

    /**
     * Returns the current working directory of the running process ON LINUX ONLY.
     * Returns null on other platforms. Also returns null if the process has finished.
     */
    override val directory
        get() = terminalResults?.process?.linuxCurrentDirectory()


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

        // TODO Do we want to clear the results when the process finishes? Maybe make this an option.

        terminalResults = null
    }

    private fun createTerminalResults(): TerminalResults {
        // Let's try to create a RealTerminalResults, but do it using reflection so that this code can be compiled
        // without all of the bloat required by JediTerm. Therefore, we have a choice of lots of bloat, but an
        // excellent terminal, or no bloat, and a naff terminal.
        try {
            // The following is a reflection version of : return RealTerminalResults(this)

            val realResultsClass = Class.forName("uk.co.nickthecoder.paratask.tools.terminal.RealTerminalResults")
            val constructor = realResultsClass.getConstructor(Tool::class.java)
            return constructor.newInstance(this) as TerminalResults

        } catch (e: Exception) {
            // println(e)
            // Fall back to using the naff, SimpleTerminalResults
            return SimpleTerminalResults(this, showCommand = showCommand, allowInput = allowInput)
        }
    }

    override fun createResults(): List<Results> {
        return singleResults(terminalResults)
    }

    override fun stop() {
        terminalResults?.stop()
    }


    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)

        tabDropHelper = object : DropFiles(dropped = { event, files -> droppedFiles(event, files) }) {

            override fun acceptTarget(event: DragEvent): Pair<Node?, Array<TransferMode>>? {
                // Can't drop files if the process isn't running, or we aren't running on linux!
                if (directory == null) {
                    return null
                }
                return super.acceptTarget(event)
            }
        }
    }

    private fun droppedFiles(event: DragEvent, files: List<File>?): Boolean {
        directory?.let {
            FileOperations.instance.fileOperation(files!!, it, event.transferMode)
        }
        return true
    }
}
