package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.LabelPosition
import uk.co.nickthecoder.paratask.parameters.asHorizontal
import uk.co.nickthecoder.paratask.parameters.compound.IntRangeParameter


class IntRangeExample : AbstractTask() {

    override val taskD = TaskDescription("intRangeExample")

    val intP = IntParameter("int")

    val range1P = IntRangeParameter("range1")
    val range2P = IntRangeParameter("range2", toText = null).asHorizontal(LabelPosition.TOP)

    init {
        taskD.addParameters(intP, range1P, range2P)
    }

    override fun run() {
        println("Range1 = from ${range1P.from} to ${range1P.to}")
        println("Range2 = from ${range2P.from} to ${range2P.to}")
    }

}

fun main(args: Array<String>) {
    TaskParser(IntRangeExample()).go(args, prompt = true)
}
