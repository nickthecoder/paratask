package uk.co.nickthecoder.paratask.tools.git

import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.tools.git.GitLogTool.GitLogRow

class GitLogTool() : uk.co.nickthecoder.paratask.tools.AbstractCommandTool<GitLogRow>() {

    override val taskD = uk.co.nickthecoder.paratask.TaskDescription("gitLog", description = "Log of Commits/Merges")

    val directoryP = uk.co.nickthecoder.paratask.parameters.FileParameter("directory", expectFile = false)

    val directory by directoryP

    val maxItemsP = uk.co.nickthecoder.paratask.parameters.IntParameter("maxItems", range = 1..Int.MAX_VALUE)

    val grepP = uk.co.nickthecoder.paratask.parameters.StringParameter("grep", required = false)

    val grepTypeP = uk.co.nickthecoder.paratask.parameters.ChoiceParameter<String>("grepType", value = "--fixed-strings",
            description = "The type of matching\nNote, Perl is still experimental")
            .choice("regular", "--basic-regexp", "Regular")
            .choice("extended", "--extended-regexp", "Extended")
            .choice("fixed", "--fixed-strings", "Fixed")
            .choice("perl", "--perl-regexp", "Perl")


    val matchCaseP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("matchCase", value = false,
            oppositeName = "ignoreCase",
            description = "Search is case sensitive")


    val mergesP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("merges", required = false)

    //TODO Replace with DateParameter
    //val untilP = StringParameter("until", required = false)

    //val beforeP = StringParameter("before", required = false)

    constructor(directory: java.io.File) : this() {
        directoryP.value = directory
    }

    init {
        directoryP.hidden = true

        taskD.addParameters(directoryP, maxItemsP, grepP, grepTypeP, mergesP, matchCaseP) //, untilP, beforeP)
    }

    override fun createColumns() {
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitLogRow, String>("date") { it.date })
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitLogRow, String>("message", width = 400) { it.message })
        columns.add(uk.co.nickthecoder.paratask.table.Column<GitLogRow, String>("author") { it.author })

    }

    override fun createCommand(): uk.co.nickthecoder.paratask.util.process.OSCommand {

        list.clear()

        val command = uk.co.nickthecoder.paratask.util.process.OSCommand("git", "log", "--date=short").dir(directory!!)

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

        //beforeP.value?.let { osCommand.addArgument( "--before=${it}")}
        //untilP.value?.let { osCommand.addArgument( "--until=${it}")}

        return command
    }

    private enum class ParseState { head, message }

    private var state = uk.co.nickthecoder.paratask.tools.git.GitLogTool.ParseState.head
    private var commit: String = ""
    private var author: String = ""
    private var message: String = ""
    private var date: String = ""

    override fun processLine(line: String) {
        if (state == uk.co.nickthecoder.paratask.tools.git.GitLogTool.ParseState.head) {
            if (line == "") {
                state = uk.co.nickthecoder.paratask.tools.git.GitLogTool.ParseState.message
            } else if (line.startsWith("commit ")) {
                commit = line.substring(7)
            } else if (line.startsWith("Author: ")) {
                author = line.substring(8)
            } else if (line.startsWith("Date:   ")) {
                date = line.substring(8)
            }
        } else if (state == uk.co.nickthecoder.paratask.tools.git.GitLogTool.ParseState.message) {
            if (line == "") {
                state = uk.co.nickthecoder.paratask.tools.git.GitLogTool.ParseState.head
                list.add(uk.co.nickthecoder.paratask.tools.git.GitLogTool.GitLogRow(commit, author, message, date))
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
    ToolParser(GitLogTool()).go(args)
}