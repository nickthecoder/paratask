package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.project.CommandLineTool
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.project.table.Column
import uk.co.nickthecoder.paratask.project.task.GitLogTool.GitLogRow
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.HasDirectory
import java.io.File

class GitLogTool() : AbstractTableTool<GitLogRow>(), HasDirectory, Stoppable {

    override val taskD = TaskDescription("gitLog", description = "Log of Commits/Merges")

    val directoryP = FileParameter("directory", expectFile = false)

    override val directory: File
        get() = directoryP.value!!

    val maxItemsP = IntParameter("maxItems", range = 1..Int.MAX_VALUE)

    val grepP = StringParameter("grep", required = false)

    val grepTypeP = ChoiceParameter<String>("grepType", value = "--fixed-strings",
            description = "The type of matching\nNote, Perl is still experimental")
            .choice("regular", "--basic-regexp", "Regular")
            .choice("extended", "--extended-regexp", "Extended")
            .choice("fixed", "--fixed-strings", "Fixed")
            .choice("perl", "--perl-regexp", "Perl")


    val matchCaseP = BooleanParameter("matchCase", value = false,
            oppositeName = "ignoreCase",
            description = "Search is case sensitive")


    val mergesP = BooleanParameter("merges", required = false)

    //TODO Replace with DateParameter
    val untilP = StringParameter("until", required = false)

    val beforeP = StringParameter("before", required = false)

    private var exec: Exec? = null

    constructor(directory: File) : this() {
        directoryP.value = directory
    }

    init {
        directoryP.hidden = true

        taskD.addParameters(directoryP, maxItemsP, grepP, grepTypeP, mergesP, matchCaseP, untilP, beforeP)
    }

    override fun createColumns() {
        columns.add(Column<GitLogRow, String>("date") { it.date })
        columns.add(Column<GitLogRow, String>("message", width = 400) { it.message })
        columns.add(Column<GitLogRow, String>("author") { it.author })

    }

    override fun run() {

        list.clear()

        val command = Command("git", "log", "--date=short").dir(directory)

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

        exec = Exec(command)
        exec?.outSink = BufferedSink { processLine(it) }
        exec?.start()?.waitFor()
    }

    override fun stop() {
        exec?.kill()
    }

    private enum class ParseState { head, message }

    private var state = ParseState.head
    private var commit: String = ""
    private var author: String = ""
    private var message: String = ""
    private var date: String = ""

    fun processLine(line: String) {
        if (state == ParseState.head) {
            if (line == "") {
                state = ParseState.message
            } else if (line.startsWith("commit ")) {
                commit = line.substring(7)
            } else if (line.startsWith("Author: ")) {
                author = line.substring(8)
            } else if (line.startsWith("Date:   ")) {
                date = line.substring(8)
            }
        } else if (state == ParseState.message) {
            if (line == "") {
                state = ParseState.head
                list.add(GitLogRow(commit, author, message, date))
                commit = ""
                author = ""
                message = ""
                date = ""
            } else {
                message = message + line.trim()
            }
        }
    }

    data class GitLogRow(val commit: String, val author: String, val message: String, val date: String) {}

}


fun main(args: Array<String>) {
    CommandLineTool(GitLogTool(File("/home/nick/projects/paratask"))).go(args)
}