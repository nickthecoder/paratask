package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import java.util.regex.Pattern

class SearchAndReplaceTask : AbstractTask() {

    override val taskD = TaskDescription("searchAndReplace")

    val filesP = MultipleParameter("files", minItems = 1) { FileParameter("file", expectFile = true, mustExist = true) }

    val searchStringP = StringParameter("searchString")

    val replaceStringP = StringParameter("replaceString")

    val useRegexP = BooleanParameter("useRegex", value = false)

    val matchCaseP = BooleanParameter("matchCase", value = true)

    init {
        taskD.addParameters( filesP, searchStringP, replaceStringP, useRegexP, matchCaseP)
    }

    override fun run() {

        val flags = (if (useRegexP.value == true) 0 else Pattern.LITERAL) + if (matchCaseP.value == true) 0 else Pattern.CASE_INSENSITIVE
        val pattern = Pattern.compile(searchStringP.value, flags)

        filesP.value.forEach { file ->
            val contents = file!!.readText()
            val matcher = pattern.matcher(contents)
            val result = matcher.replaceAll(replaceStringP.value)
            file.writeText(result)
        }
    }
}