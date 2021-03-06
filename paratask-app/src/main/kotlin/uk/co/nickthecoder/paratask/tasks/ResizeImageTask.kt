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

class ResizeImageTask : AbstractCommandTask() {

    val inputFileP = FileParameter("inputFile", label = "Input Image")

    val outputFileP = FileParameter("outputFile", label = "Output Image", mustExist = null)

    val percentageP = IntParameter("percentage")

    val widthP = IntParameter("width", label = "")

    val heightP = IntParameter("height", label = "x")

    val keepAspectRationP = BooleanParameter("keepAspectRatio", value = true)

    val sizeP = SimpleGroupParameter("size")
            .addParameters(widthP, heightP, keepAspectRationP)
            .asHorizontal(LabelPosition.LEFT)

    val resizeOptionP = OneOfParameter("resizeOptions", choiceLabel = "Resize Type", value = sizeP)
            .addChoices(sizeP, percentageP)

    val onlyShrinkP = BooleanParameter("onlyShrink", value = true)

    override val taskD = TaskDescription("resizeImage")
            .addParameters(inputFileP, resizeOptionP, sizeP, percentageP, outputFileP, outputP)

    override fun createCommand(): OSCommand {
        var size: String

        if (resizeOptionP.value === sizeP) {
            size = "${widthP.value}x${heightP.value}"
            if (keepAspectRationP.value == false) {
                size += "!"
            }
            if (onlyShrinkP.value == true) {
                size += ">"
            }
        } else {
            size = "${percentageP.value}%"
        }

        val osCommand = OSCommand("convert", inputFileP.value, "-resize", size, outputFileP.value)

        return osCommand
    }
}


fun main(args: Array<String>) {
    TaskParser(ResizeImageTask()).go(args)
}
