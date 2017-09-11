package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter


class IntRangeExample : AbstractTask() {

    override val taskD = TaskDescription("intRangeExample")

    val rangeP = IntRangeParameter("range")

    init {
        taskD.addParameters(rangeP)
    }

    override fun run() {
        println("Range = from ${rangeP.from} to ${rangeP.to}")
    }

}

fun main(args: Array<String>) {
    TaskParser(IntRangeExample()).go(args, prompt = true)
}
