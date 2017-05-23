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

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.tools.git.GitLogTool.GitLogRow
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.time.format.DateTimeFormatter

class GitLogTool : AbstractCommandTool<GitLogRow>() {

    override val taskD = TaskDescription("gitLog", description = "Log of Commits/Merges")

    val directoryP = FileParameter("directory", expectFile = false)

    val directory by directoryP

    val maxItemsP = IntParameter("maxItems", range = 1..Int.MAX_VALUE)

    val grepP = StringParameter("grep", required = false)

    val grepTypeP = ChoiceParameter("grepType", value = "--fixed-strings",
            description = "The type of matching\nNote, Perl is still experimental")
            .choice("regular", "--basic-regexp", "Regular")
            .choice("extended", "--extended-regexp", "Extended")
            .choice("fixed", "--fixed-strings", "Fixed")
            .choice("perl", "--perl-regexp", "Perl")


    val matchCaseP = BooleanParameter("matchCase", value = false,
            oppositeName = "ignoreCase",
            description = "Search is case sensitive")


    val mergesP = BooleanParameter("merges", required = false)

    val sinceP = DateParameter("since", required = false)

    val untilP = DateParameter("until", required = false)

    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        taskD.addParameters(directoryP, maxItemsP, grepP, grepTypeP, mergesP, matchCaseP, sinceP, untilP)
    }

    override fun createColumns() {
        columns.add(Column<GitLogRow, String>("date") { it.date })
        columns.add(Column<GitLogRow, String>("message", width = 400) { it.message })
        columns.add(Column<GitLogRow, String>("author") { it.author })
    }

    override fun createCommand(): OSCommand {

        val command = OSCommand("git", "log", "--date=short").dir(directory!!)

        if (grepP.value != "") {
            command.addArguments(grepTypeP.value, "--grep=${grepP.value}")
        }

        if (matchCaseP.value == false) {
            command.addArgument("--regexp-ignore-case")
        }

        if (mergesP.value == true) {
            command.addArgument("--merges")
        }
        if (mergesP.value == false) {
            command.addArgument("--no-merges")
        }

        sinceP.value?.let { command.addArgument("--since=${dateFormat.format(it)}") }
        untilP.value?.let { command.addArgument("--until=${dateFormat.format(it)}") }

        state = GitLogTool.ParseState.head

        println("since=${sinceP.value} until=${untilP.value} commnad: ${command}")
        return command
    }

    private enum class ParseState { head, message }

    private var state = GitLogTool.ParseState.head
    private var commit: String = ""
    private var author: String = ""
    private var message: String = ""
    private var date: String = ""

    override fun processLine(line: String) {
        if (state == GitLogTool.ParseState.head) {
            if (line == "") {
                state = GitLogTool.ParseState.message
            } else if (line.startsWith("commit ")) {
                commit = line.substring(7)
            } else if (line.startsWith("Author: ")) {
                author = line.substring(8)
            } else if (line.startsWith("Date:   ")) {
                date = line.substring(8)
            }
        } else if (state == GitLogTool.ParseState.message) {
            if (line == "") {
                state = GitLogTool.ParseState.head
                list.add(GitLogTool.GitLogRow(commit, author, message, date))
                commit = ""
                author = ""
                message = ""
                date = ""
            } else {
                message += line.trim()
            }
        }
    }

    data class GitLogRow(val commit: String, val author: String, val message: String, val date: String)

}


fun main(args: Array<String>) {
    ToolParser(GitLogTool()).go(args)
}