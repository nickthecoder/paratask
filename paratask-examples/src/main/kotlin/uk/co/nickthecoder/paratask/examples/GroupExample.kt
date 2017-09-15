package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter

class GroupExample : AbstractTask() {

    override val taskD = TaskDescription("groupExample")

    val normalStringP = StringParameter("normalString")
    val normalGroupP = GroupParameter("normalGroup")

    val horizontalStringP = StringParameter("horizontalString")
    val horizontalGroupP = GroupParameter("horizontalGroup")

    init {
        normalGroupP.addParameters(normalStringP)
        horizontalGroupP.addParameters(horizontalStringP)
        taskD.addParameters(normalGroupP, horizontalGroupP)

        horizontalGroupP.horizontalLayout(false)
    }

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(GroupExample()).go(args, prompt = true)
}
