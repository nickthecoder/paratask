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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class GitRMTask() : AbstractTask() {
    override val taskD = TaskDescription("gitRM")

    val directoryP = FileParameter("directory", expectFile = false)

    val filesP = MultipleParameter<File?>("files", minItems = 1) { FileParameter("", expectFile = null, baseDirectoryP = directoryP) }

    val optionP = ChoiceParameter<String?>("option", value = null, required = false)
            .choice("none", null, "<default>")
            .choice("-f", "-f", "Force Removal")
            .choice("--cache", "--cache", "Keep File (remove from cache)")

    init {
        taskD.addParameters(directoryP, filesP, optionP)
    }

    override fun run(): OSCommand {
        val command = OSCommand("git", "rm", optionP.value, "--", filesP.stringValue).dir(directoryP.value!!)

        return command
    }
}
