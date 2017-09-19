package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter

val zeroMeters = ScaledDouble(0.0, 1000.0, mapOf(1.0 to "mm", 10.0 to "cm", 1000.0 to "m"))

class ScaledDoubleExample : AbstractTask() {


    val lengthP = ScaledDoubleParameter("length", value = zeroMeters)
    var length by lengthP

    override val taskD = TaskDescription("scaledDoubleExample")
            .addParameters(lengthP)


    override fun run() {
        println("Length = $length (${length.value} mm)")
    }

}

fun main(args: Array<String>) {
    TaskParser(ScaledDoubleExample()).go(args, prompt = true)
}
