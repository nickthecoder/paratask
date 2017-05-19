package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.CommandLineTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.util.Command


class GrepTask() : AbstractTask() {

    override val taskD = TaskDescription(
            name = "grep",
            description = "Search a file or recursively through a directory tree")

    val fileP = FileParameter("file", label = "File or Directory",
            description = "Search a single file, or a whole directory tree", expectFile = null)

    val matchP = MultipleParameter<String>("match", minItems = 1,
            description = "The regular expression to search for"
    ) { StringParameter("") }

    val partP = ChoiceParameter<String>("part", value = "",
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

    val matchCaseP = BooleanParameter("matchCase", value = false,
            oppositeName = "ignoreCase",
            description = "Search is case sensitive")

    val invertResultsP = BooleanParameter("invertResults", value = false,
            description = "List files NOT matching the regular expression")

    val followSymLinksP = BooleanParameter("followSymLinks", value = false,
            description = "Follow symbolic links when searching recursively")

    val maxMatchesP = IntParameter("maxMatches", value = null, required = false,
            description = "The maximum number of matches to show per file.\nLeave blank to show ALL matches.")
            .min(1)

    val contextLinesP = IntParameter("contextLines", required = false,
            description = "Output number of lines of context surrounding the matched line").min(1)

    val additionalOptionsP = StringParameter("additionalOptions", value = "IHsn",
            description = "Additional grep parameters (default=IHsn)")

    init {
        taskD.addParameters(
                fileP, matchP, matchCaseP,
                typeP, partP, invertResultsP, followSymLinksP,
                maxMatchesP, contextLinesP, additionalOptionsP)
    }

    override fun run(): Command {

        val rOrR = if (followSymLinksP.value == true) "-R" else "-r"

        val command = Command("grep", typeP.value, rOrR)

        val additionalOptions = additionalOptionsP.value
        if (additionalOptions != "") {
            command.addArgument("-" + additionalOptions)
        }

        if (invertResultsP.value == true) {
            command.addArgument("-L")

            val maxMatches = maxMatchesP.value
            if (maxMatches != null) {
                command.addArgument("-m");
                command.addArgument(maxMatches);
            }
        }
        if (matchCaseP.value == false) {
            command.addArgument("-i")
        }

        partP.value?.let { command.addArgument(it) }

        maxMatchesP.value?.let { command.addArgument("--max-count=${it}") }

        contextLinesP.value?.let {
            command.addArgument("-C")
            command.addArgument(it)
        }

        matchP.value.forEach { value ->
            command.addArgument("-e")
            command.addArgument(value)
        }

        command.addArgument("--")
        command.addArgument(fileP.value)

        return command
    }
}

fun main(args: Array<String>) {
    CommandLineTask(GrepTask()).go(args)
}
