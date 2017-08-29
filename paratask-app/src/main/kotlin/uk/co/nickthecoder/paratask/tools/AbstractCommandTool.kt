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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ButtonParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.HeaderOrFooter
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.tools.terminal.TerminalTool
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

abstract class AbstractCommandTool<T : Any> : ListTableTool<T>(), Stoppable {

    protected var exec: Exec? = null

    /**
     * Used to show the command string in the footer
     */
    val footerTask = FooterTask()

    override fun run() {

        list.clear()

        val command = createCommand()
        footerTask.commandP.value = command.toString()

        val exec = Exec(command)
        exec.outSink = BufferedSink { processLine(it) }
        exec.start().waitFor()
        execFinished()
    }

    override fun stop() {
        exec?.kill()
    }

    abstract fun processLine(line: String)

    open fun execFinished() {}

    abstract fun createCommand(): OSCommand

    override fun createFooter() = HeaderOrFooter(footerTask.commandP, footerTask.runP)

    inner class FooterTask : AbstractTask() {
        override val taskD = TaskDescription("command")

        val commandP = StringParameter("command")
        val runP = ButtonParameter("run", label = "", buttonText = "Run") {
            val tool = TerminalTool(createCommand())
            toolPane?.halfTab?.projectTab?.let { projectTab ->
                projectTab.projectTabs.addAfter(projectTab, tool)
            }
        }

        init {
            taskD.addParameters(commandP, runP)
        }

        override fun run() {

        }
    }

}