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

package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.project.SharedToolPane
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.HasDirectory
import java.io.File

/**
 * Combines multiple git tools into one.
 */
class GitTool : AbstractTool(), HasDirectory {

    override val taskD = TaskDescription("git", description = "Source Code Control")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory: File? by directoryP

    val gitStatus = GitStatusTool()

    val gitLog = GitLogTool()

    val gitStash = GitStashTool()

    val logGroupP = GroupParameter("gitLog")

    var resultsTabIndex: Int = 0

    init {
        logGroupP.addParameters(
                gitLog.maxItemsP.link(), gitLog.grepP.link(), gitLog.grepTypeP.link(), gitLog.mergesP.link(),
                gitLog.matchCaseP.link(), gitLog.sinceP.link(), gitLog.untilP.link())

        taskD.addParameters(directoryP, logGroupP)
    }

    override fun createHeader() = Header(this, directoryP)

    override fun createResults(): List<Results> {
        return gitStatus.createResults() + gitLog.createResults() + gitStash.createResults()
    }

    override fun updateResults() {
        super.updateResults()
        toolPane?.tabPane?.selectionModel?.select(resultsTabIndex)
    }

    override fun run() {
        resultsTabIndex = toolPane?.tabPane?.selectionModel?.selectedIndex ?: 0

        gitStatus.directoryP.value = directoryP.value
        gitLog.directoryP.value = directoryP.value
        gitStash.directoryP.value = directoryP.value

        gitStatus.run()
        gitLog.run()
        gitStash.run()
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        gitStatus.toolPane = SharedToolPane(this)
        gitLog.toolPane = SharedToolPane(this)
        gitStash.toolPane = SharedToolPane(this)
    }
}

fun main(args: Array<String>) {
    TaskParser(GitTool()).go(args)
}
