package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.CommandTask
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.util.Command
import java.util.concurrent.TimeUnit

class GrepTask() : SimpleTask() {

    override val taskD = TaskDescription(
            name = "example",
            description = "Search in Files Recursively")

    val regexP = StringParameter("regex")

    val directoryP = FileParameter("directory")

    /*
    val type: StringChoiceParameter StringChoiceParameter("type")
        .choice("G", "Regular"),
        .choice("E", "Extended"),
        .choice("F", "Fixed"),
        .choice("P", "Perl")

     */

    val matchCaseP = BooleanParameter("matchCase", value = false)

    val matchWordsP = BooleanParameter("matchWords", value = false)
    //description = "Force pattern to match only whole words")

    val matchLinesP = BooleanParameter("matchLines", value = false)
    //description = "Force pattern to match only whole lines")

    val invertResultsP = BooleanParameter("invertResults", value = false)
    //description = "List files NOT matching the pattern")

    val maxMatchesP = IntParameter("maxMatches", value = 1, range = 1..100)

    init {
        taskD.addParameters(regexP, directoryP, matchCaseP, matchWordsP, matchLinesP, invertResultsP, maxMatchesP)
    }

    override fun run(values: Values) {

        val command = Command("grep", "-rHsn") // + type)

        if (invertResultsP.value(values) == true) {
            command.addArgument("-L")

            command.addArgument("-m");
            command.addArgument(maxMatchesP.value(values));
        }
        if (matchCaseP.value(values) == false) {
            command.addArgument("-i")
        }
        if (matchWordsP.value(values) == true) {
            command.addArgument("-w")
        }
        if (matchLinesP.value(values) == true) {
            command.addArgument("-x")
        }

        command.addArgument("--")
        command.addArgument(regexP.value(values))
        command.addArgument(directoryP.value(values))

        command.createExec().inheritOut().inheritErr().start().waitFor(5, TimeUnit.MINUTES)
    }

}

fun main(args: Array<String>) {
    CommandTask(GrepTask()).go(args)
}
