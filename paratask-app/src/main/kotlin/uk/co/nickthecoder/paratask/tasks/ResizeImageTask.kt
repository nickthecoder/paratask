package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.AbstractCommandTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.process.OSCommand

class ResizeImageTask : AbstractCommandTask() {

    override val taskD = TaskDescription("resizeImage")

    val inputFileP = FileParameter("inputFile", label = "Input Image")

    val outputFileP = FileParameter("outputFile", label = "Output Image", mustExist = null)

    val resizeOptionP = OneOfParameter("resizeOption")

    val percentageP = IntParameter("percentage")

    val widthP = IntParameter("width")

    val heightP = IntParameter("height")

    val sizeP = GroupParameter("size")

    val keepAspectRationP = BooleanParameter("keepAspectRatio", value = true)

    val onlyShrinkP = BooleanParameter("onlyShrink", value = true)

    init {
        sizeP.addParameters(widthP, heightP, keepAspectRationP)
        resizeOptionP.addParameters(sizeP, percentageP)

        taskD.addParameters(inputFileP, resizeOptionP, outputFileP, outputP)
    }

    override fun createCommand(): OSCommand {
        var size: String

        if (resizeOptionP.value === sizeP) {
            size = "${widthP.value}x${heightP.value}"
            if (keepAspectRationP.value == false) {
                size = size + "!"
            }
            if (onlyShrinkP.value == true) {
                size = size + ">"
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
