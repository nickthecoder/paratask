package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.CommandTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.parameter.enumChoices
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Duration
import uk.co.nickthecoder.paratask.util.Labelled
import java.io.OutputStream
import java.util.concurrent.TimeUnit

enum class Output(override val label: String) : Labelled {
    PASSTHROUGH("Terminal"), GUI("GUI")
}

class GrepTask() : ExecTask() {

    override val taskD = TaskDescription(
            name = "grep",
            description = "Search File Contents Recursively")

    val fileP = FileParameter("file", label = "File or Directory",
            description = "Search a single file, or a whole directory tree")

    val regexP = StringParameter("regex",
            description = "The regular expression to search for")

    val matchP = ChoiceParameter<String>("match", value = "",
            description = "Match a word, a line or any part of the file")
            .choice("any", "", "Any Part")
            .choice("word", "-w", "Word")
            .choice("line", "-x", "Line")

    val typeP = ChoiceParameter<String>("type", value = "-E",
            description = "The type of matching\nNote, Perl is still experimental")
            .choice("regular", "-G", "Regular")
            .choice("extended", "-E", "Extended")
            .choice("fixed", "-F", "Fixed")
            .choice("perl", "-P", "Perl")

    val matchCaseP = BooleanParameter("matchCase", value = false)

    val invertResultsP = BooleanParameter("invertResults", value = false,
            description = "List files NOT matching the regular expression")

    val followSymLinksP = BooleanParameter("followSymLinks", value = false,
            description = "Follow symbolic links when searching recursively")

    val maxMatchesP = IntParameter("maxMatches", value = null, range = 1..Int.MAX_VALUE, required = false,
            description = "The maximum number of matches to show per file.\nLeave blank to show ALL matches.")

    val additionalOptionsP = StringParameter("additionalOptions", value = "Hsn")

    init {
        taskD.addParameters(
                fileP, regexP, matchCaseP,
                typeP, matchP, invertResultsP, followSymLinksP,
                maxMatchesP, additionalOptionsP, outputP)
    }

    override fun command(values: Values): Command {

        val rOrR = if (followSymLinksP.value(values) == true) "-R" else "-r"

        val command = Command("grep", typeP.value(values), rOrR)

        val additionalOptions = additionalOptionsP.value(values)
        if (additionalOptions != "") {
            command.addArgument("-" + additionalOptions)
        }

        if (invertResultsP.value(values) == true) {
            command.addArgument("-L")

            val maxMatches = maxMatchesP.value(values)
            if (maxMatches != null) {
                command.addArgument("-m");
                command.addArgument(maxMatches);
            }
        }
        if (matchCaseP.value(values) == false) {
            command.addArgument("-i")
        }

        val match = matchP.value(values)
        if (match != "") {
            command.addArgument(match)
        }

        command.addArgument("-e")
        command.addArgument(regexP.value(values))

        command.addArgument("--")
        command.addArgument(fileP.value(values))

        println(command)
        return command
    }
}

fun main(args: Array<String>) {
    CommandTask(GrepTask()).go(args)
}
