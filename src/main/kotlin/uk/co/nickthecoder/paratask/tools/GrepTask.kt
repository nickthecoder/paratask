package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand


class GrepTask : AbstractTask() {

    override val taskD = TaskDescription(
            name = "grep",
            description = "Search a single file or recursively through a directory tree")


    val filesP = MultipleParameter("filesOrDirectories", minItems = 1,
            description = "Files or Directories to search"
    ) { FileParameter("", expectFile = null) }

    val patternsP = MultipleParameter("patternsToMatch", minItems = 1,
            description = "Pattern(s) to search for"
    ) { StringParameter("") }

    val partP = ChoiceParameter("part", value = "",
            description = "Match a word, a line or any part of the file")
            .choice("any", "", "Any Part")
            .choice("word", "-w", "Word")
            .choice("line", "-x", "Line")

    val typeP = ChoiceParameter("type", value = "-E",
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
                filesP, patternsP, matchCaseP,
                typeP, partP, invertResultsP, followSymLinksP,
                maxMatchesP, contextLinesP, additionalOptionsP)
        taskD.unnamedParameter = filesP
    }

    override fun run(): OSCommand {

        val rOrR = if (followSymLinksP.value == true) "-R" else "-r"

        val command = OSCommand("grep", typeP.value, rOrR)

        val additionalOptions = additionalOptionsP.value
        if (additionalOptions != "") {
            command.addArgument("-" + additionalOptions)
        }

        if (invertResultsP.value == true) {
            command.addArgument("-L")

            val maxMatches = maxMatchesP.value
            if (maxMatches != null) {
                command.addArgument("-m")
                command.addArgument(maxMatches)
            }
        }
        if (matchCaseP.value == false) {
            command.addArgument("-i")
        }

        partP.value?.let { command.addArgument(it) }

        maxMatchesP.value?.let { command.addArgument("--max-count=$it") }

        contextLinesP.value?.let {
            command.addArgument("-C")
            command.addArgument(it)
        }

        patternsP.value.forEach { value ->
            command.addArgument("-e")
            command.addArgument(value)
        }

        command.addArgument("--")

        filesP.value.forEach { file ->
            command.addArgument(file)
        }

        return command
    }
}

fun main(args: Array<String>) {
    TaskParser(GrepTask()).go(args)
}
