package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter

class GroupExample : AbstractTask() {

    override val taskD = TaskDescription("groupExample")

    val normalStringP = StringParameter("normalString")
    val normalGroupP = GroupParameter("normalGroup")

    val horizontalIntP = IntParameter("horizontalInt", label = "Int")
    val horizontalStringP = StringParameter("horizontalString", label = "String")
    val horizontalGroupP = GroupParameter("horizontalGroup")

    val boolAP = BooleanParameter("a")
    val boolBP = BooleanParameter("b")
    val noStretchP = GroupParameter("No stretchy fields")

    val boolCP = BooleanParameter("c")
    val midStringP = StringParameter("midStr", label = "String")
    val boolDP = BooleanParameter("d")
    val middleStretchyP = GroupParameter("middleStretchy")

    init {
        normalGroupP.addParameters(normalStringP)
        horizontalGroupP.addParameters(horizontalIntP, horizontalStringP)
        noStretchP.addParameters(boolAP, boolBP)
        middleStretchyP.addParameters(boolCP, midStringP, boolDP)

        taskD.addParameters(normalGroupP, horizontalGroupP, noStretchP, middleStretchyP)

        horizontalGroupP.horizontalLayout(false)
        noStretchP.horizontalLayout(false)
        middleStretchyP.horizontalLayout(false)
    }

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(GroupExample()).go(args, prompt = true)
}
