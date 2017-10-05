package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class VerticalGroupExample : AbstractTask() {


    val verticalIntP = IntParameter("verticalInt", label = "Int", minValue = 1)
    val verticalStringP = StringParameter("verticalString", label = "String", columns = 10)
    val verticalGroupP = SimpleGroupParameter("verticalGroup")
            .addParameters(verticalIntP, verticalStringP)
            .asVertical(isBoxed = true)


    val info2P = InformationParameter("info2", information = "\nHere we see a group without labels, and the first StringParameter is made non-stretchy.")

    val houseNumberP = StringParameter("houseNumber", columns = 6, stretchy = false)
    val roadNameP = StringParameter("roadName")
    val addressLine1P = SimpleGroupParameter("addressLine1")
            .addParameters(houseNumberP, roadNameP)
            .asVertical(LabelPosition.NONE, isBoxed = true)

    override val taskD = TaskDescription("verticalGroupExample",
            description = "Demonstrates the use of SimpleGroupParameter.asVertical(…)")
            .addParameters(verticalGroupP, info2P, addressLine1P)


    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(VerticalGroupExample()).go(args, prompt = true)
}
