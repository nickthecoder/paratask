package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class GroupExample : AbstractTask() {

    val normalIntP = IntParameter("normalInt", label = "Int")
    val normalStringP = StringParameter("normalString", label = "String", columns = 10)
    val normalGroupP = SimpleGroupParameter("normalGroup")
            .addParameters(normalIntP, normalStringP)

    val plainIntP = IntParameter("plainInt", label = "Int")
    val plainStringP = StringParameter("plainString", label = "String", columns = 10)
    val plainGroupP = SimpleGroupParameter("plainGroup")
            .addParameters(plainIntP, plainStringP).asPlain()

    override val taskD = TaskDescription("groupExample",
            description = """Demonstrates how to group parameters.

There are other ways to lay out groups. See HorizontalGroupExample, and GridGroupExample.""")
            .addParameters(normalGroupP, plainGroupP)

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(GroupExample()).go(args, prompt = true)
}
