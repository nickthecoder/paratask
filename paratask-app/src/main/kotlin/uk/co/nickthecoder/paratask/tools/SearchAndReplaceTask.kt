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