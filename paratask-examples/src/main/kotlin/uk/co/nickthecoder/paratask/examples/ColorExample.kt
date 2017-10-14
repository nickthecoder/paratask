package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.AlphaColorParameter
import uk.co.nickthecoder.paratask.parameters.ColorParameter

class ColorExample : AbstractTask() {

    val colorP = ColorParameter("color")

    val alphaColorP = AlphaColorParameter("alphaColor")

    override val taskD = TaskDescription("colorExample",
            description = "Shows a ColorParameter in action.")
            .addParameters(colorP, alphaColorP)

    override fun run() {
        println("Color = ${colorP.value}")
        println("Alpha Color = ${alphaColorP.value}")
    }

}

fun main(args: Array<String>) {
    TaskParser(ColorExample()).go(args, prompt = true)
}
