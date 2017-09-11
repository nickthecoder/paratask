package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.compound.TemporalAmountParameter

class TemporalAmountExample : AbstractTask() {

    override val taskD = TaskDescription("temporalAmountExample")

    val periodP = TemporalAmountParameter("period")

    init {
        taskD.addParameters(periodP)
    }

    override fun run() {
        println("Period = ${periodP.amount} ${periodP.units?.label} : ${periodP.value}")
    }

}

fun main(args: Array<String>) {
    TaskParser(TemporalAmountExample()).go(args, prompt = true)
}
