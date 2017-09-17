/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.AbstractCommandTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.process.OSCommand

class FlipImageTask : AbstractCommandTask() {

    override val taskD = TaskDescription("flipImage")

    val inputFileP = FileParameter("inputFile", label = "Input Image")

    val outputFileP = FileParameter("outputFile", label = "Output Image", mustExist = null)

    val directionP = ChoiceParameter("direction", value = "x")

    init {

        directionP.addChoice("x", "x", "Mirror Left/Right")
        directionP.addChoice("y", "y", "Upside Down")
        taskD.addParameters(inputFileP, directionP, outputFileP, outputP)
    }

    override fun createCommand(): OSCommand {
        val direction = if (directionP.value == "x") "-flop" else "-flip"
        val osCommand = OSCommand("convert", inputFileP.value, direction, outputFileP.value)
        return osCommand
    }
}

fun main(args: Array<String>) {
    TaskParser(FlipImageTask()).go(args)
}
