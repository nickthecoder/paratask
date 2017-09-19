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

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.tools.places.DirectoryTool
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.linuxCurrentDirectory

class TerminalTool() : AbstractTerminalTool(showCommand = true, allowInput = true) {

    constructor(command: OSCommand) : this() {
        programP.value = command.program
        argumentsP.value = command.arguments
        directoryP.value = command.directory
    }

    val programP = StringParameter("program", value = "bash")

    val argumentsP = MultipleParameter("arguments") { StringParameter("", required = false) }

    val directoryP = FileParameter("directory", expectFile = false, required = false)

    val titleP = StringParameter(name = "title", value = "Terminal")

    val closeWhenFinishedP = BooleanParameter("closeWhenFinished", value = false)

    override val taskD = TaskDescription("terminal", description = "A simple terminal emulator")
            .addParameters(programP, argumentsP, directoryP, titleP, closeWhenFinishedP)


    override fun run() {
        shortTitle = titleP.value
        longTitle = "${titleP.value} ${programP.value} ${argumentsP.value.joinToString(separator = " ")}"
        super.run()
    }

    override fun finished() {
        if (closeWhenFinishedP.value == true) {
            toolPane?.halfTab?.close()
        }
    }

    fun input(value: Boolean): TerminalTool {
        allowInput = value
        return this
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand(programP.value)
        argumentsP.value.forEach { arg ->
            command.addArgument(arg)
        }
        command.directory = directoryP.value
        return command
    }

    /**
     * Splits the view, so that there's a DirectoryTool on the right.
     * If there is ALREADY a directory tool on the right, then update it's directory to match the terminal's current working directory
     * This only works on Linux, as it uses /proc/
     */
    fun syncDirectoryTool() {
        val directory = terminalResults?.process?.linuxCurrentDirectory()
        directory ?: return

        val otherHalf = toolPane?.halfTab?.otherHalf()
        if (otherHalf == null) {
            val tool = DirectoryTool()
            tool.directoriesP.value = listOf(directory)
            toolPane?.halfTab?.projectTab?.split(tool)

        } else {
            updateSyncedDirectoryTool()
        }
    }

    private fun updateSyncedDirectoryTool() {
        val otherHalf = toolPane?.halfTab?.otherHalf()
        if (otherHalf != null) {
            val otherTool = otherHalf.toolPane.tool
            if (otherTool is DirectoryTool) {
                val oldValue = otherTool.directoriesP.value
                if (otherTool.directoriesP.innerParameters.isEmpty()) {
                    otherTool.directoriesP.addValue(directory)
                } else {
                    otherTool.directoriesP.innerParameters[0].value = directory
                }
                if (oldValue != otherTool.directoriesP.value) {
                    otherTool.toolPane?.skipFocus = true
                    otherTool.toolPane?.parametersPane?.run()
                }
            }
        }
    }

    override fun createTerminalResults(): TerminalResults {
        val results = super.createTerminalResults()

        results.node.addEventFilter(KeyEvent.KEY_RELEASED) { event ->
            if (event.code == KeyCode.ENTER) {
                updateSyncedDirectoryTool()
            }
        }

        return results
    }
}
