package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.process.OSCommand

class GitStashTool : AbstractCommandTool<GitStashRow>() {

    override val taskD = TaskDescription("gitStash")

    val directoryP = FileParameter("directory", expectFile = false)

    override val resultsName = "Stash"

    init {
        taskD.addParameters(directoryP)
    }


    override fun createColumns(): List<Column<GitStashRow, *>> {
        val columns = mutableListOf<Column<GitStashRow, *>>()

        columns.add(Column<GitStashRow, String?>("stash") { it.name })
        columns.add(Column<GitStashRow, String?>("basedOnCommit") { it.message })
        columns.add(Column<GitStashRow, String?>("commit") { it.commit })

        return columns
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("git", "stash", "list", "--format=full")

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

            list.add(GitStashRow(parsedName, parsedCommit, parsedMessage))

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
