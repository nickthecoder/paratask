package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.CommandTask
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command
import java.util.concurrent.TimeUnit

class GrepTask() : SimpleTask() {

    override val taskD = TaskDescription(
            name = "grep",
            description = "Search File Contents Recursively")

    val fileP = FileParameter("file", label = "File or Directory")

    val regexP = StringParameter("regex")

    val matchP = ChoiceParameter<String>("match", value = "")
            .choice("any", "", "Anywhere")
            .choice("word", "-w", "Word")
            .choice("line", "-x", "Line")

    val typeP = ChoiceParameter<String>("type", value = "-E")
            .choice("regular", "-G", "Regular")
            .choice("extended", "-E", "Extended")
            .choice("fixed", "-F", "Fixed")
            .choice("perl", "-P", "Perl")

    val matchCaseP = BooleanParameter("matchCase", value = false)

    val invertResultsP = BooleanParameter("invertResults", value = false)
    //description = "List files NOT matching the pattern")

    val followSymLinksP = BooleanParameter("followSymLinks", value = false)

    val maxMatchesP = IntParameter("maxMatches", value = 100, range = 1..Int.MAX_VALUE)

    val additionalOptionsP = StringParameter("additionalOptions", value = "Hsn")

    init {
        taskD.addParameters(
                fileP, regexP, matchCaseP,
                typeP, matchP, invertResultsP, followSymLinksP,
                maxMatchesP, additionalOptionsP)
    }

    override fun run(values: Values) {

        val rOrR = if (followSymLinksP.value(values) == true) "-R" else "-r"

        val command = Command("grep", typeP.value(values), rOrR)

        val additionalOptions = additionalOptionsP.value(values)
        if (additionalOptions != "") {
            command.addArgument("-" + additionalOptions)
        }

        if (invertResultsP.value(values) == true) {
            command.addArgument("-L")

            command.addArgument("-m");
            command.addArgument(maxMatchesP.value(values));
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
        command.createExec().inheritOut().inheritErr().start().waitFor(5, TimeUnit.MINUTES)
    }

}

fun main(args: Array<String>) {
    CommandTask(GrepTask()).go(args)
}
