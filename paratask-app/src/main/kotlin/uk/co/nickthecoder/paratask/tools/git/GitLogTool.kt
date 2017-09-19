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
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.tools.git.GitLogTool.GitLogRow
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.time.format.DateTimeFormatter

class GitLogTool :
        AbstractCommandTool<GitLogRow>(),
        HasDirectory,
        SingleRowFilter<GitLogRow> {

    override val taskD = TaskDescription("gitLog", description = "Log of Commits/Merges")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory by directoryP

    val maxItemsP = IntParameter("maxItems", minValue = 1, value = 100)

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

    override val resultsName = "Log"

    private val exampleRow = GitLogRow("", "", "", "")

    init {
        taskD.addParameters(directoryP, maxItemsP, grepP, grepTypeP, mergesP, matchCaseP, sinceP, untilP)

        columns.add(Column<GitLogRow, String>("date", getter = { it.date }))
        columns.add(Column<GitLogRow, String>("message", width = 400, getter = { it.message }))
        columns.add(Column<GitLogRow, String>("author", getter = { it.author }))
    }

    override val rowFilter = RowFilter(this, columns, exampleRow, "Git Log Filter")


    override fun run() {
        longTitle = "Git Log ${directory}"
        super.run()
    }

    override fun createCommand(): OSCommand {

        longTitle = "Git Log $directory"

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

        state = GitLogTool.ParseState.HEAD

        return command
    }

    private enum class ParseState { HEAD, MESSAGE }

    private var state = GitLogTool.ParseState.HEAD
    private var commit: String = ""
    private var author: String = ""
    private var message: String = ""
    private var date: String = ""

    override fun processLine(line: String) {
        if (state == GitLogTool.ParseState.HEAD) {
            if (line == "") {
                state = GitLogTool.ParseState.MESSAGE
            } else if (line.startsWith("commit ")) {
                commit = line.substring(7)
            } else if (line.startsWith("Author: ")) {
                author = line.substring(8)
            } else if (line.startsWith("Date:   ")) {
                date = line.substring(8)
            }
        } else if (state == GitLogTool.ParseState.MESSAGE) {
            if (line == "") {
                state = GitLogTool.ParseState.HEAD
                if (list.size < maxItemsP.value!!) {
                    val row = GitLogTool.GitLogRow(commit, author, message, date)
                    if (rowFilter.accept(row)) {
                        list.add(row)
                    }
                }
                commit = ""
                author = ""
                message = ""
                date = ""
            } else {
                message += line.trim()
            }
        }
    }

    override fun execFinished() {
        processLine("") // Fake an extra blank line to finish off the last message.
    }

    data class GitLogRow(val commit: String, val author: String, val message: String, val date: String)

}


fun main(args: Array<String>) {
    TaskParser(GitLogTool()).go(args)
}