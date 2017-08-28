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
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.process.OSCommand

class RotateImageTask : AbstractCommandTask() {

    override val taskD = TaskDescription("rotateImage")

    val inputFileP = FileParameter("inputFile", label = "Input Image")

    val outputFileP = FileParameter("outputFile", label = "Output Image", mustExist = null)

    val rotationP = ChoiceParameter<Int>("rotation", value = 0)

    init {
        rotationP.addChoice("0", 0, "No Rotation")
        rotationP.addChoice("90", 90, "90° Clockwise")
        rotationP.addChoice("180", 180, "180°")
        rotationP.addChoice("-90", -90, "90° Anticlockwise")
        taskD.addParameters(inputFileP, rotationP, outputFileP, outputP)
    }

    override fun createCommand(): OSCommand {
        val osCommand = OSCommand("convert", inputFileP.value, "-rotate", rotationP.value, outputFileP.value)
        return osCommand
    }
}


fun main(args: Array<String>) {
    TaskParser(RotateImageTask()).go(args)
}
