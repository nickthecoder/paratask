/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.OSCommand

class GitStashTool : AbstractCommandTool<GitStashRow>(), HasDirectory {

    override val taskD = TaskDescription("gitStash")

    val directoryP = FileParameter("directory", expectFile = false)

    override val resultsName = "Stash"

    override val directory by directoryP

    override val rowFilter = RowFilter<GitStashRow>(this, columns, GitStashRow("", "", ""))


    init {
        taskD.addParameters(directoryP)

        columns.add(Column<GitStashRow, String?>("stash") { it.name })
        columns.add(Column<GitStashRow, String?>("basedOnCommit") { it.message })
        columns.add(Column<GitStashRow, String?>("commit") { it.commit })
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("git", "stash", "list", "--format=full")
        command.directory = directory

        return command
    }

    private var parsedName: String = ""
    var parsedCommit: String = ""
    var parsedMessage: String = ""

    override fun processLine(line: String) {
        val namePrefix = "Reflog: refs/"
        val commitPrefix = "commit "
        val messagePrefix = "Reflog message: "

        if (line.startsWith(commitPrefix)) {
            parsedCommit = line.substring(commitPrefix.length)

        } else if (line.startsWith(namePrefix)) {
            parsedName = line.substring(namePrefix.length).split(" ")[0]

        } else if (line.startsWith(messagePrefix)) {
            parsedMessage = line.substring(messagePrefix.length)

            val row = GitStashRow(parsedName, parsedCommit, parsedMessage)
            list.add(row)

            parsedName = ""
            parsedCommit = ""
            parsedMessage = ""
        }
    }

}

data class GitStashRow(
        val name: String,
        val commit: String,
        val message: String
)
