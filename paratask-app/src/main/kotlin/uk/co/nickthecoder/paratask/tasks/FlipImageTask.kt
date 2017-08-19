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

    val directionP = ChoiceParameter<String>("direction", value = "x")

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
