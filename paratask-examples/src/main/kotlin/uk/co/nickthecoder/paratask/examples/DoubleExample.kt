package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.DoubleParameter

class DoubleExample : AbstractTask() {

    val lengthP = DoubleParameter("length", value = 0.0)
    var length by lengthP

    override val taskD = TaskDescription("doubleExample")
            .addParameters(lengthP)

    override fun run() {
        println("Length = $length")
    }

}

fun main(args: Array<String>) {
    TaskParser(DoubleExample()).go(args, prompt = true)
}
